package ru.bartwell.kick.module.explorer.feature.list.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.module.explorer.feature.list.util.FileSystemUtils

internal class DefaultFileExplorerComponent(
    componentContext: ComponentContext,
    private val onFinished: () -> Unit,
    private val onViewFile: (String) -> Unit,
    private val onShowOptions: (String) -> Unit,
) : FileExplorerComponent, ComponentContext by componentContext {

    private val _model = MutableValue(FileExplorerState())
    override val model: Value<FileExplorerState> = _model

    override fun init(context: PlatformContext) {
        loadDirectory(FileSystemUtils.getInitialDirectory(context))
    }

    private fun loadDirectory(path: String) {
        val entries = FileSystemUtils.listDirectory(path)
        val canGoUp = FileSystemUtils.getParentPath(path) != null
        _model.value = model.value.copy(currentPath = path, entries = entries, canGoUp = canGoUp)
    }

    override fun onUpClick() {
        val parent = FileSystemUtils.getParentPath(model.value.currentPath)
        if (parent != null) {
            loadDirectory(parent)
        }
    }

    override fun onDirectoryClick(name: String) {
        val current = model.value.currentPath
        val path = if (current.endsWith("/")) {
            current + name
        } else {
            "$current/$name"
        }
        loadDirectory(path)
    }

    private fun buildFilePath(name: String): String {
        val current = model.value.currentPath
        return if (current.endsWith("/")) {
            current + name
        } else {
            "$current/$name"
        }
    }

    override fun onFileClick(name: String) {
        onShowOptions(buildFilePath(name))
    }

    override fun onKnownFolderClick(path: String) {
        loadDirectory(path)
    }

    override fun onBackClick() {
        onFinished()
    }
}
