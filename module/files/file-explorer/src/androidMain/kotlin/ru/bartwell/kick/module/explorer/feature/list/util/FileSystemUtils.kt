package ru.bartwell.kick.module.explorer.feature.list.util

import android.os.Environment
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.core.data.get
import ru.bartwell.kick.module.explorer.feature.list.data.FileEntry
import java.io.File

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal actual object FileSystemUtils {

    actual fun getInitialDirectory(context: PlatformContext): String {
        val files = context.get().filesDir
        return files.parentFile?.absolutePath ?: files.absolutePath
    }

    actual fun listDirectory(path: String): List<FileEntry> {
        val dir = File(path)
        return dir.listFiles()?.map {
            FileEntry(
                name = it.name,
                isDirectory = it.isDirectory,
                size = if (it.isFile) it.length() else null,
                lastModified = it.lastModified()
            )
        }?.sortedWith(compareByDescending<FileEntry> { it.isDirectory }.thenBy { it.name }) ?: emptyList()
    }

    actual fun getKnownFolders(context: PlatformContext): List<KnownFolder> {
        val folders = mutableListOf<KnownFolder>()
        folders += KnownFolder("App", getInitialDirectory(context))
        val external = Environment.getExternalStorageDirectory()
        if (external.exists() && external.canRead()) {
            folders += KnownFolder("External Storage", external.absolutePath)
        }
        return folders
    }

    actual fun getParentPath(path: String): String? {
        return File(path).parent
    }
}
