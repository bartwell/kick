package ru.bartwell.kick.module.controlpanel

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import ru.bartwell.kick.core.component.Child
import ru.bartwell.kick.core.component.Config
import ru.bartwell.kick.core.component.StubConfig
import ru.bartwell.kick.core.data.Module
import ru.bartwell.kick.core.data.ModuleDescription
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.module.controlpanel.data.ControlPanelItem
import ru.bartwell.kick.module.controlpanel.persists.ControlPanelSettings

@Suppress("UnusedPrivateProperty", "EmptyFunctionBlock", "unused")
public class ControlPanelModule(
    context: PlatformContext,
    items: List<ControlPanelItem>,
) : Module {

    override val description: ModuleDescription = ModuleDescription.CONTROL_PANEL
    override val startConfig: Config = StubConfig(description)

    init {
        ControlPanelSettings(items)
    }

    override fun getComponent(
        componentContext: ComponentContext,
        nav: StackNavigation<Config>,
        config: Config,
    ): Child<*>? = null

    @Composable
    override fun Content(instance: Child<*>) {
    }

    override fun registerSubclasses(builder: PolymorphicModuleBuilder<Config>) {
    }
}
