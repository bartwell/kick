package ru.bartwell.kick.module.multiplatformsettings

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.russhwolf.settings.Settings
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import ru.bartwell.kick.core.component.Child
import ru.bartwell.kick.core.component.Config
import ru.bartwell.kick.core.component.StubConfig
import ru.bartwell.kick.core.data.Module
import ru.bartwell.kick.core.data.ModuleDescription

@Suppress("UnusedPrivateProperty", "EmptyFunctionBlock", "unused")
public class MultiplatformSettingsModule(
    private val settingsList: List<Pair<String, Settings>> = emptyList()
) : Module {

    override val description: ModuleDescription = ModuleDescription.MULTIPLATFORM_SETTINGS
    override val startConfig: Config = StubConfig(description)

    override fun getComponent(
        componentContext: ComponentContext,
        nav: StackNavigation<Config>,
        config: Config,
    ): Child<*>? = null

    @Composable
    override fun Content(instance: Child<*>) {
    }

    override fun registerSubclasses(builder: PolymorphicModuleBuilder<Config>) {}
}
