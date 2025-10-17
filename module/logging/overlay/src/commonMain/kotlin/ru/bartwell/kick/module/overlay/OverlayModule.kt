package ru.bartwell.kick.module.overlay

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pop
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import ru.bartwell.kick.Kick
import ru.bartwell.kick.core.component.Child
import ru.bartwell.kick.core.component.Config
import ru.bartwell.kick.core.data.Module
import ru.bartwell.kick.core.data.ModuleDescription
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.module.overlay.core.component.child.OverlayChild
import ru.bartwell.kick.module.overlay.core.component.config.OverlayConfig
import ru.bartwell.kick.module.overlay.core.overlay.KickOverlay
import ru.bartwell.kick.module.overlay.core.persists.OverlaySettings
import ru.bartwell.kick.module.overlay.core.provider.OverlayProvider
import ru.bartwell.kick.module.overlay.core.provider.OverlayProviderHandle
import ru.bartwell.kick.module.overlay.core.provider.PerformanceOverlayProvider
import ru.bartwell.kick.module.overlay.core.store.OverlayStore
import ru.bartwell.kick.module.overlay.feature.settings.presentation.DefaultOverlayComponent
import ru.bartwell.kick.module.overlay.feature.settings.presentation.OverlayContent

public class OverlayModule(
    private val context: PlatformContext,
    private val providers: List<OverlayProvider> = listOf(PerformanceOverlayProvider()),
) : Module {
    override val description: ModuleDescription = ModuleDescription.OVERLAY
    override val startConfig: Config = OverlayConfig

    private val providerScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val activeProviderHandles: MutableMap<OverlayProvider, ActiveProvider> = mutableMapOf()

    init {
        OverlaySettings(context)
        // Initialize selected category from persisted settings
        OverlayStore.selectCategory(OverlaySettings.getSelectedCategory())
        KickOverlay.init(context)
        if (OverlaySettings.isEnabled()) {
            KickOverlay.show()
        }

        OverlayStore.declareCategories(providers.flatMap { it.categories })

        providerScope.launch {
            OverlayStore.selectedCategory.collectLatest { category ->
                updateProvidersFor(category)
            }
        }
    }

    private fun updateProvidersFor(selectedCategory: String) {
        providers.forEach { provider ->
            val active = activeProviderHandles[provider]
            if (provider.categories.contains(selectedCategory)) {
                if (active?.category != selectedCategory) {
                    active?.handle?.stop()
                    val handle = provider.start(providerScope, Kick.overlay, selectedCategory)
                    activeProviderHandles[provider] = ActiveProvider(selectedCategory, handle)
                }
            } else if (active != null) {
                active.handle.stop()
                activeProviderHandles.remove(provider)
            }
        }
    }

    private data class ActiveProvider(
        val category: String,
        val handle: OverlayProviderHandle,
    )

    override fun getComponent(
        componentContext: ComponentContext,
        nav: StackNavigation<Config>,
        config: Config,
    ): Child<*>? = if (config is OverlayConfig) {
        OverlayChild(
            DefaultOverlayComponent(
                componentContext = componentContext,
                onEnabledChangeCallback = { enabled -> if (enabled) KickOverlay.show() else KickOverlay.hide() },
                onBackCallback = { nav.pop() },
            )
        )
    } else {
        null
    }

    @Composable
    override fun Content(instance: Child<*>) {
        when (val child = instance) {
            is OverlayChild -> OverlayContent(
                component = child.component,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }

    override fun registerSubclasses(builder: PolymorphicModuleBuilder<Config>) {
        builder.subclass(OverlayConfig::class, OverlayConfig.serializer())
    }
}
