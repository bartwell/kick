package ru.bartwell.kick.module.explorer.feature.list.presentation

import ru.bartwell.kick.module.explorer.feature.list.data.FileEntry

internal data class FileExplorerState(
    val currentPath: String = "",
    val entries: List<FileEntry> = emptyList(),
    val canGoUp: Boolean = true,
    val selectedFileName: String? = null,
    val exportedFilePath: String? = null,
    val error: String? = null,
) {
    val folderName: String
        get() = currentPath
            .replace('\\', '/')
            .substringAfterLast('/', currentPath)
}
