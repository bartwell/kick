package ru.bartwell.kick.module.explorer

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import ru.bartwell.kick.core.component.Child
import ru.bartwell.kick.core.component.Config
import ru.bartwell.kick.core.data.Module
import ru.bartwell.kick.core.data.ModuleDescription
import ru.bartwell.kick.module.explorer.core.component.child.FileExplorerChild
import ru.bartwell.kick.module.explorer.core.component.child.FileViewerChild
import ru.bartwell.kick.module.explorer.core.component.child.FileOptionsChild
import ru.bartwell.kick.module.explorer.core.component.config.FileExplorerConfig
import ru.bartwell.kick.module.explorer.core.component.config.FileViewerConfig
import ru.bartwell.kick.module.explorer.core.component.config.FileOptionsConfig
import ru.bartwell.kick.module.explorer.feature.list.presentation.DefaultFileExplorerComponent
import ru.bartwell.kick.module.explorer.feature.list.presentation.FileExplorerContent
import ru.bartwell.kick.module.explorer.feature.options.presentation.DefaultFileOptionsComponent
import ru.bartwell.kick.module.explorer.feature.options.presentation.FileOptionsContent
import ru.bartwell.kick.module.explorer.feature.viewer.presentation.DefaultFileViewerComponent
import ru.bartwell.kick.module.explorer.feature.viewer.presentation.FileViewerContent

public class FileExplorerModule : Module {
    override val description: ModuleDescription = ModuleDescription.FILE_EXPLORER
    override val startConfig: Config = FileExplorerConfig

    override fun getComponent(
        componentContext: ComponentContext,
        nav: StackNavigation<Config>,
        config: Config
    ): Child<*>? = when (config) {
        is FileExplorerConfig -> FileExplorerChild(
            DefaultFileExplorerComponent(
                componentContext = componentContext,
                onFinished = { nav.pop() },
                onViewFile = { path -> nav.pushNew(FileViewerConfig(path)) },
                onShowOptions = { path -> nav.pushNew(FileOptionsConfig(path)) }
            )
        )
        is FileViewerConfig -> FileViewerChild(
            DefaultFileViewerComponent(
                componentContext = componentContext,
                filePath = config.path,
                onFinished = { nav.pop() }
            )
        )
        is FileOptionsConfig -> FileOptionsChild(
            DefaultFileOptionsComponent(
                componentContext = componentContext,
                filePath = config.path,
                onViewFile = { path -> nav.pushNew(FileViewerConfig(path)) },
                onFinished = { nav.pop() }
            )
        )
        else -> null
    }

    @Composable
    override fun Content(instance: Child<*>) {
        when (val child = instance) {
            is FileExplorerChild -> FileExplorerContent(modifier = Modifier.fillMaxSize(), component = child.component)
            is FileViewerChild -> FileViewerContent(modifier = Modifier.fillMaxSize(), component = child.component)
            is FileOptionsChild -> FileOptionsContent(component = child.component)
        }
    }

    override fun registerSubclasses(builder: PolymorphicModuleBuilder<Config>) {
        builder.subclass(FileExplorerConfig::class, FileExplorerConfig.serializer())
        builder.subclass(FileViewerConfig::class, FileViewerConfig.serializer())
        builder.subclass(FileOptionsConfig::class, FileOptionsConfig.serializer())
    }
}
