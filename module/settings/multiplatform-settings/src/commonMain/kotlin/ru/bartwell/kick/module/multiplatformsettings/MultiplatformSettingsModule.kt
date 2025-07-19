package ru.bartwell.kick.module.multiplatformsettings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.russhwolf.settings.Settings
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import ru.bartwell.kick.core.component.Child
import ru.bartwell.kick.core.component.Config
import ru.bartwell.kick.core.data.Module
import ru.bartwell.kick.core.data.ModuleDescription
import ru.bartwell.kick.module.multiplatformsettings.core.component.child.SettingsEditorChild
import ru.bartwell.kick.module.multiplatformsettings.core.component.child.SettingsListChild
import ru.bartwell.kick.module.multiplatformsettings.core.component.config.SettingsEditorConfig
import ru.bartwell.kick.module.multiplatformsettings.core.component.config.SettingsListConfig
import ru.bartwell.kick.module.multiplatformsettings.feature.editor.presentation.DefaultSettingsEditorComponent
import ru.bartwell.kick.module.multiplatformsettings.feature.editor.presentation.SettingsEditorContent
import ru.bartwell.kick.module.multiplatformsettings.feature.list.presentation.DefaultSettingsListComponent
import ru.bartwell.kick.module.multiplatformsettings.feature.list.presentation.SettingsListContent

public class MultiplatformSettingsModule(
    private val settingsList: List<Pair<String, Settings>> = emptyList()
) : Module {

    override val description: ModuleDescription = ModuleDescription.MULTIPLATFORM_SETTINGS
    override val startConfig: Config = SettingsListConfig

    override fun getComponent(
        componentContext: ComponentContext,
        nav: StackNavigation<Config>,
        config: Config,
    ): Child<*>? = when (config) {
        is SettingsListConfig -> SettingsListChild(
            DefaultSettingsListComponent(
                componentContext = componentContext,
                settingsList = settingsList,
                onFinished = { nav.pop() },
                onStorageClick = { storageName -> nav.pushNew(SettingsEditorConfig(storageName)) },
            )
        )

        is SettingsEditorConfig -> {
            val settings = settingsList.find { it.first == config.storageName }?.second
            if (settings != null) {
                SettingsEditorChild(
                    DefaultSettingsEditorComponent(
                        componentContext = componentContext,
                        storageName = config.storageName,
                        settings = settings,
                        onFinished = { nav.pop() },
                    )
                )
            } else {
                null
            }
        }

        else -> null
    }

    @Composable
    override fun Content(instance: Child<*>) {
        when (val child = instance) {
            is SettingsListChild -> SettingsListContent(
                component = child.component,
                modifier = Modifier.fillMaxSize(),
            )
            is SettingsEditorChild -> SettingsEditorContent(
                component = child.component,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }

    override fun registerSubclasses(builder: PolymorphicModuleBuilder<Config>) {
        builder.subclass(SettingsEditorConfig::class, SettingsEditorConfig.serializer())
        builder.subclass(SettingsListConfig::class, SettingsListConfig.serializer())
    }
}
