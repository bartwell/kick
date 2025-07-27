package ru.bartwell.kick.module.configuration

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
import ru.bartwell.kick.module.configuration.core.component.child.ConfigurationChild
import ru.bartwell.kick.module.configuration.core.component.config.ConfigurationConfig
import ru.bartwell.kick.module.configuration.core.persists.ConfigurationSettings
import ru.bartwell.kick.module.configuration.data.ConfigurationItem
import ru.bartwell.kick.module.configuration.feature.presentation.ConfigurationContent
import ru.bartwell.kick.module.configuration.feature.presentation.DefaultConfigurationComponent

public class ConfigurationModule(
    context: PlatformContext,
    private val items: List<ConfigurationItem>,
) : Module {

    override val description: ModuleDescription = ModuleDescription.CONFIGURATION
    override val startConfig: Config = ConfigurationConfig

    init {
        ConfigurationSettings(context = context, configuration = items)
    }

    override fun getComponent(
        componentContext: ComponentContext,
        nav: StackNavigation<Config>,
        config: Config,
    ): Child<*>? = if (config is ConfigurationConfig) {
        ConfigurationChild(
            DefaultConfigurationComponent(
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
            is ConfigurationChild -> ConfigurationContent(
                component = child.component,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }

    override fun registerSubclasses(builder: PolymorphicModuleBuilder<Config>) {
        builder.subclass(ConfigurationConfig::class, ConfigurationConfig.serializer())
    }
}
