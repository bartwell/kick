package ru.bartwell.kick.module.explorer.feature.list.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.module.explorer.feature.list.data.Result
import ru.bartwell.kick.module.explorer.feature.list.util.FileSystemUtils

internal class DefaultFileExplorerComponent(
    componentContext: ComponentContext,
    private val onFinished: () -> Unit,
    private val onFileOpen: (String) -> Unit
) : FileExplorerComponent, ComponentContext by componentContext {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val _model = MutableValue(FileExplorerState())
    override val model: Value<FileExplorerState> = _model

    override fun init(context: PlatformContext) {
        scope.launch {
            val start = FileSystemUtils.getInitialDirectory(context)
            loadDirectoryInternal(start)
        }
    }

    private suspend fun loadDirectoryInternal(path: String) {
        val entries = FileSystemUtils.listDirectory(path)
        val canGoUp = FileSystemUtils.getParentPath(path) != null
        _model.value = model.value.copy(currentPath = path, entries = entries, canGoUp = canGoUp)
    }

    override fun onUpClick() {
        val parent = FileSystemUtils.getParentPath(model.value.currentPath)
        if (parent != null) {
            scope.launch { loadDirectoryInternal(parent) }
        }
    }

    override fun onDirectoryClick(name: String) {
        val path = model.value.currentPath.appendToPath(name)
        scope.launch { loadDirectoryInternal(path) }
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
        onFileOpen(model.value.currentPath.appendToPath(fileName))
    }

    override fun onDownloadClick(context: PlatformContext) {
        val fileName = model.value.selectedFileName ?: return
        _model.value = model.value.copy(selectedFileName = null)
        val path = model.value.currentPath.appendToPath(fileName)
        scope.launch {
            val result = FileSystemUtils.exportFile(context, path)
            when (result) {
                is Result.Success -> _model.value = model.value.copy(exportedFilePath = result.data)
                is Result.Error -> _model.value = model.value.copy(error = result.message)
            }
        }
    }

    override fun onExportAlertDismiss() {
        _model.value = model.value.copy(exportedFilePath = null)
    }

    override fun onDeleteClick() {
        val fileName = model.value.selectedFileName ?: return
        _model.value = model.value.copy(selectedFileName = null, fileToDelete = fileName)
    }

    override fun onDeleteConfirm() {
        val state = model.value
        val fileName = state.fileToDelete ?: return
        val path = state.currentPath.appendToPath(fileName)
        scope.launch {
            when (val result = FileSystemUtils.deleteFile(path)) {
                is Result.Success -> {
                    loadDirectoryInternal(state.currentPath)
                    _model.value = model.value.copy(fileToDelete = null)
                }
                is Result.Error -> {
                    _model.value = model.value.copy(fileToDelete = null, error = result.message)
                }
            }
        }
    }

    override fun onDeleteDismiss() {
        _model.value = model.value.copy(fileToDelete = null)
    }

    override fun onKnownFolderClick(path: String) {
        scope.launch { loadDirectoryInternal(path) }
    }

    override fun onErrorAlertDismiss() {
        _model.value = model.value.copy(error = null)
    }

    override fun onBackClick() {
        onFinished()
    }
}

private fun String.appendToPath(name: String): String {
    return if (endsWith("/") || endsWith("\\")) {
        this + name
    } else {
        "$this/$name"
    }
}
