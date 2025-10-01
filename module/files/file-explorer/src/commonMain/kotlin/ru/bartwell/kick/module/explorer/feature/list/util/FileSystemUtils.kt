package ru.bartwell.kick.module.explorer.feature.list.util

import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.module.explorer.feature.list.data.FileEntry
import ru.bartwell.kick.module.explorer.feature.list.data.Result

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal expect object FileSystemUtils {
    suspend fun getInitialDirectory(context: PlatformContext): String
    suspend fun listDirectory(path: String): List<FileEntry>
    suspend fun getKnownFolders(context: PlatformContext): List<KnownFolder>
    fun getParentPath(path: String): String?
    suspend fun readText(path: String): Result
    suspend fun exportFile(context: PlatformContext, path: String): Result
    suspend fun deleteFile(path: String): Result
}

internal data class KnownFolder(val name: String, val path: String)
