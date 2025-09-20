package ru.bartwell.kick.module.overlay

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pop
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
import ru.bartwell.kick.module.overlay.feature.settings.presentation.DefaultOverlayComponent
import ru.bartwell.kick.module.overlay.feature.settings.presentation.OverlayContent

public class OverlayModule(private val context: PlatformContext) : Module {
    override val description: ModuleDescription = ModuleDescription.OVERLAY
    override val startConfig: Config = OverlayConfig

    init {
        OverlaySettings(context)
        KickOverlay.init(context)
        if (OverlaySettings.isEnabled()) {
            KickOverlay.show(context)
        }
    }

    override fun getComponent(
        componentContext: ComponentContext,
        nav: StackNavigation<Config>,
        config: Config,
    ): Child<*>? = if (config is OverlayConfig) {
        OverlayChild(
            DefaultOverlayComponent(
                componentContext = componentContext,
                onEnabledChangeCallback = { enabled -> if (enabled) KickOverlay.show(context) else KickOverlay.hide() },
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
