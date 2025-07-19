package ru.bartwell.kick.module.explorer

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
import ru.bartwell.kick.module.explorer.core.component.child.FileExplorerChild
import ru.bartwell.kick.module.explorer.core.component.config.FileExplorerConfig
import ru.bartwell.kick.module.explorer.feature.list.presentation.DefaultFileExplorerComponent
import ru.bartwell.kick.module.explorer.feature.list.presentation.FileExplorerContent

public class FileExplorerModule : Module {
    override val description: ModuleDescription = ModuleDescription.FILE_EXPLORER
    override val startConfig: Config = FileExplorerConfig

    override fun getComponent(
        componentContext: ComponentContext,
        nav: StackNavigation<Config>,
        config: Config
    ): Child<*>? = if (config is FileExplorerConfig) {
        FileExplorerChild(
            DefaultFileExplorerComponent(
                componentContext = componentContext,
                onFinished = { nav.pop() }
            )
        )
    } else {
        null
    }

    @Composable
    override fun Content(instance: Child<*>) {
        when (val child = instance) {
            is FileExplorerChild -> FileExplorerContent(modifier = Modifier.fillMaxSize(), component = child.component)
        }
    }

    override fun registerSubclasses(builder: PolymorphicModuleBuilder<Config>) {
        builder.subclass(FileExplorerConfig::class, FileExplorerConfig.serializer())
    }
}
