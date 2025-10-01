package ru.bartwell.kick.module.controlpanel

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
import ru.bartwell.kick.module.controlpanel.core.component.child.ControlPanelChild
import ru.bartwell.kick.module.controlpanel.core.component.config.ControlPanelConfig
import ru.bartwell.kick.module.controlpanel.core.persists.ControlPanelSettings
import ru.bartwell.kick.module.controlpanel.data.ControlPanelItem
import ru.bartwell.kick.module.controlpanel.feature.presentation.ControlPanelContent
import ru.bartwell.kick.module.controlpanel.feature.presentation.DefaultControlPanelComponent

public class ControlPanelModule(
    context: PlatformContext,
    private val items: List<ControlPanelItem>,
) : Module {

    override val description: ModuleDescription = ModuleDescription.CONTROL_PANEL
    override val startConfig: Config = ControlPanelConfig

    init {
        ControlPanelSettings(context = context, configuration = items)
    }

    override fun getComponent(
        componentContext: ComponentContext,
        nav: StackNavigation<Config>,
        config: Config,
    ): Child<*>? = if (config is ControlPanelConfig) {
        ControlPanelChild(
            DefaultControlPanelComponent(
                componentContext = componentContext,
                items = items,
                onFinished = { nav.pop() },
            )
        )
    } else {
        null
    }

    @Composable
    override fun Content(instance: Child<*>) {
        when (val child = instance) {
            is ControlPanelChild -> ControlPanelContent(
                component = child.component,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }

    override fun registerSubclasses(builder: PolymorphicModuleBuilder<Config>) {
        builder.subclass(ControlPanelConfig::class, ControlPanelConfig.serializer())
    }
}
