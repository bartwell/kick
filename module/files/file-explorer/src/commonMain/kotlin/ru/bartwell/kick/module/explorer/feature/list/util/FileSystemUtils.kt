package ru.bartwell.kick.module.explorer.feature.list.util

import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.module.explorer.feature.list.data.FileEntry
import ru.bartwell.kick.module.explorer.feature.list.data.Result

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal expect object FileSystemUtils {
    fun getInitialDirectory(context: PlatformContext): String
    fun listDirectory(path: String): List<FileEntry>
    fun getKnownFolders(context: PlatformContext): List<KnownFolder>
    fun getParentPath(path: String): String?
    fun readText(path: String): Result
    fun exportFile(context: PlatformContext, path: String): Result
}

internal data class KnownFolder(val name: String, val path: String)
