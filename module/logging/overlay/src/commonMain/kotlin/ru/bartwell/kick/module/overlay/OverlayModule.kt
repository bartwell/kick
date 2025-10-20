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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.serialization.modules.PolymorphicModuleBuilder
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
import ru.bartwell.kick.module.overlay.core.provider.PerformanceOverlayProvider
import ru.bartwell.kick.module.overlay.core.store.OverlayStore
import ru.bartwell.kick.module.overlay.feature.settings.presentation.DefaultOverlayComponent
import ru.bartwell.kick.module.overlay.feature.settings.presentation.OverlayContent

public class OverlayModule(
    context: PlatformContext,
    private val providers: List<OverlayProvider> = listOf(PerformanceOverlayProvider()),
) : Module {
    override val description: ModuleDescription = ModuleDescription.OVERLAY
    override val startConfig: Config = OverlayConfig

    private val providerScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    init {
        OverlaySettings(context)
        observeFloatingWindowState()
        OverlayStore.selectCategory(OverlaySettings.getSelectedCategory())
        KickOverlay.init(context)
        if (OverlaySettings.isEnabled()) {
            KickOverlay.show()
        }
    }

    private fun observeFloatingWindowState() {
        combine(OverlaySettings.observeEnabled(), OverlayStore.selectedCategory) { isWindowEnabled, currentCategory ->
            providers.forEach { provider ->
                provider.categories.forEach { providerCategory ->
                    OverlayStore.addCategory(providerCategory)
                    if (isWindowEnabled && providerCategory == currentCategory && provider.isAvailable) {
                        provider.start(providerScope)
                    } else {
                        provider.stop()
                    }
                }
            }
        }
            .launchIn(providerScope)
    }

    override fun getComponent(
        componentContext: ComponentContext,
        nav: StackNavigation<Config>,
        config: Config,
    ): Child<*>? = if (config is OverlayConfig) {
        OverlayChild(
            DefaultOverlayComponent(
                componentContext = componentContext,
                providers = providers,
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
