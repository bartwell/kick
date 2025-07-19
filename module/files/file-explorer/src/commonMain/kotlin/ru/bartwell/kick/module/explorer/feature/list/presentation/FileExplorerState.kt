package ru.bartwell.kick.module.explorer.feature.list.presentation

import ru.bartwell.kick.module.explorer.feature.list.data.FileEntry

internal data class FileExplorerState(
    val currentPath: String = "",
    val entries: List<FileEntry> = emptyList(),
    val canGoUp: Boolean = true,
) {
    val folderName: String
        get() = currentPath.substringAfterLast('/', currentPath)
}
