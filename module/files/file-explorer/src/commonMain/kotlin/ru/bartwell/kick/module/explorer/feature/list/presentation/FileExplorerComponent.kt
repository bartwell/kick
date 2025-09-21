package ru.bartwell.kick.module.explorer.feature.list.presentation

import com.arkivanov.decompose.value.Value
import ru.bartwell.kick.core.component.Component
import ru.bartwell.kick.core.data.PlatformContext

@Suppress("TooManyFunctions")
internal interface FileExplorerComponent : Component {
    val model: Value<FileExplorerState>
    fun init(context: PlatformContext)
    fun onUpClick()
    fun onDirectoryClick(name: String)
    fun onFileClick(name: String)
    fun onFileActionDismiss()
    fun onViewAsTextClick()
    fun onDownloadClick(context: PlatformContext)
    fun onExportAlertDismiss()
    fun onDeleteClick()
    fun onDeleteConfirm()
    fun onDeleteDismiss()
    fun onKnownFolderClick(path: String)
    fun onErrorAlertDismiss()
    fun onBackClick()
}
