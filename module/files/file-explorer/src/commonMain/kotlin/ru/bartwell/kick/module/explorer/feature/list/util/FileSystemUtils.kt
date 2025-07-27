package ru.bartwell.kick.module.explorer.feature.list.util

import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.module.explorer.feature.list.data.FileEntry

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal expect object FileSystemUtils {
    fun getInitialDirectory(context: PlatformContext): String
    fun listDirectory(path: String): List<FileEntry>
    fun getKnownFolders(context: PlatformContext): List<KnownFolder>
    fun getParentPath(path: String): String?
    fun readFileText(path: String): String
    /**
     * Copies the file located at [path] to a user-accessible directory and
     * returns absolute destination path if the operation was successful.
     */
    fun exportFile(context: PlatformContext, path: String): String?
}

internal data class KnownFolder(val name: String, val path: String)
