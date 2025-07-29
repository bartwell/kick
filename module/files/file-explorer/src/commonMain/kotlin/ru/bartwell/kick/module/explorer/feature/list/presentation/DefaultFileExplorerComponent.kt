package ru.bartwell.kick.module.explorer.feature.list.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.module.explorer.feature.list.data.Result
import ru.bartwell.kick.module.explorer.feature.list.util.FileSystemUtils

internal class DefaultFileExplorerComponent(
    componentContext: ComponentContext,
    private val onFinished: () -> Unit,
    private val onFileOpen: (String) -> Unit
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

    private fun fullPath(name: String): String {
        val current = model.value.currentPath
        return if (current.endsWith("/")) {
            current + name
        } else {
            "$current/$name"
        }
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

    override fun onFileClick(name: String) {
        _model.value = model.value.copy(selectedFileName = name)
    }

    override fun onFileActionDismiss() {
        _model.value = model.value.copy(selectedFileName = null)
    }

    override fun onViewAsTextClick() {
        val fileName = model.value.selectedFileName ?: return
        _model.value = model.value.copy(selectedFileName = null)
        onFileOpen(fullPath(fileName))
    }

    override fun onDownloadClick(context: PlatformContext) {
        val fileName = model.value.selectedFileName ?: return
        _model.value = model.value.copy(selectedFileName = null)
        val path = fullPath(fileName)
        val result = FileSystemUtils.exportFile(context, path)
        when (result) {
            is Result.Success -> _model.value = model.value.copy(exportedFilePath = result.data)
            is Result.Error -> _model.value = model.value.copy(error = result.message)
        }
    }

    override fun onExportAlertDismiss() {
        _model.value = model.value.copy(exportedFilePath = null)
    }

    override fun onKnownFolderClick(path: String) {
        loadDirectory(path)
    }

    override fun onErrorAlertDismiss() {
        _model.value = model.value.copy(error = null)
    }

    override fun onBackClick() {
        onFinished()
    }
}
