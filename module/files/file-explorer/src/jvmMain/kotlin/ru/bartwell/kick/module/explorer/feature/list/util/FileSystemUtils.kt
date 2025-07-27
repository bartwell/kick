package ru.bartwell.kick.module.explorer.feature.list.util

import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.module.explorer.feature.list.data.FileEntry
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal actual object FileSystemUtils {

    actual fun getInitialDirectory(context: PlatformContext): String {
        return System.getProperty("user.home")
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
        val home = Paths.get(System.getProperty("user.home"))
        val tmpDir = Paths.get(System.getProperty("java.io.tmpdir"))

        val basic = listOf(
            "Home" to home,
            "Documents" to home.resolve("Documents"),
            "Downloads" to home.resolve("Downloads"),
            "Temp" to tmpDir
        )

        val codeSource = FileSystemUtils::class.java.protectionDomain.codeSource
        val appDir: Path? = codeSource
            ?.location
            ?.toURI()
            ?.let { File(it).parentFile?.toPath() }

        val all = buildList {
            addAll(basic)
            appDir?.let { add("App Home" to it) }
        }

        return all.mapNotNull { (label, path) ->
            if (path != null && Files.exists(path)) {
                KnownFolder(label, path.toAbsolutePath().toString())
            } else {
                null
            }
        }
    }

    actual fun getParentPath(path: String): String? {
        return File(path).parent
    }

    actual fun readFileText(path: String): String = File(path).readText()

    actual fun exportFile(context: PlatformContext, path: String): String? {
        val src = File(path)
        val dest = Paths.get(System.getProperty("user.home"), "Downloads", src.name).toFile()
        return try {
            src.copyTo(dest, overwrite = true)
            dest.absolutePath
        } catch (_: IOException) {
            null
        }
    }
}
