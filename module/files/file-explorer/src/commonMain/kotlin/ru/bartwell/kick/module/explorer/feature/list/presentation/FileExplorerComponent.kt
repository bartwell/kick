package ru.bartwell.kick.module.explorer.feature.list.presentation

import com.arkivanov.decompose.value.Value
import ru.bartwell.kick.core.component.Component
import ru.bartwell.kick.core.data.PlatformContext

internal interface FileExplorerComponent : Component {
    val model: Value<FileExplorerState>
    fun init(context: PlatformContext)
    fun onUpClick()
    fun onDirectoryClick(name: String)
    fun onFileClick(name: String)
    fun onKnownFolderClick(path: String)
    fun onBackClick()
}
