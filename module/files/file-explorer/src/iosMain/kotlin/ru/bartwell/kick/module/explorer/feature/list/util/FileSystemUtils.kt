package ru.bartwell.kick.module.explorer.feature.list.util

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSDate
import platform.Foundation.NSFileManager
import platform.Foundation.NSDirectoryEnumerationSkipsHiddenFiles
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.NSNumber
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.NSURLContentModificationDateKey
import platform.Foundation.NSURLFileSizeKey
import platform.Foundation.NSURLIsDirectoryKey
import platform.Foundation.NSURLNameKey
import platform.Foundation.NSUserDomainMask
import platform.Foundation.allObjects
import platform.Foundation.timeIntervalSince1970
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.module.explorer.feature.list.data.FileEntry

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal actual object FileSystemUtils {

    private const val MILLIS_PER_SECOND = 1_000L
    private val fileManager = NSFileManager.defaultManager

    actual fun getInitialDirectory(context: PlatformContext): String {
        val dirs = NSSearchPathForDirectoriesInDomains(
            NSDocumentDirectory,
            NSUserDomainMask,
            true
        ).mapNotNull { it as? String }
        return dirs.firstOrNull().orEmpty()
    }

    @OptIn(ExperimentalForeignApi::class)
    actual fun listDirectory(path: String): List<FileEntry> {
        val url = NSURL.fileURLWithPath(path)
        val keys = listOf(
            NSURLNameKey,
            NSURLIsDirectoryKey,
            NSURLFileSizeKey,
            NSURLContentModificationDateKey
        )
        val enumerator = fileManager.enumeratorAtURL(
            url,
            keys,
            NSDirectoryEnumerationSkipsHiddenFiles,
            null
        )

        @Suppress("UNCHECKED_CAST")
        val urls = enumerator?.allObjects as? List<NSURL> ?: emptyList()

        return urls.mapNotNull { nsUrl ->
            val name = nsUrl.lastPathComponent ?: return@mapNotNull null
            val attrs = nsUrl.resourceValuesForKeys(keys, null) ?: return@mapNotNull null

            val isDir = (attrs[NSURLIsDirectoryKey] as? NSNumber)?.boolValue ?: false
            val size = (attrs[NSURLFileSizeKey] as? NSNumber)?.longValue
            val lastModified = (attrs[NSURLContentModificationDateKey] as? NSDate)
                ?.timeIntervalSince1970
                ?.times(MILLIS_PER_SECOND)
                ?.toLong()
                ?: 0L

            FileEntry(
                name = name,
                isDirectory = isDir,
                size = size,
                lastModified = lastModified
            )
        }.sortedWith(
            compareByDescending<FileEntry> { it.isDirectory }
                .thenBy { it.name }
        )
    }

    actual fun getKnownFolders(context: PlatformContext): List<KnownFolder> {
        val folderTypes = listOf(
            NSDocumentDirectory to "Documents",
            NSCachesDirectory to "Caches",
            NSApplicationSupportDirectory to "Application Support"
        )

        val standardFolders = folderTypes.flatMap { (directory, label) ->
            NSSearchPathForDirectoriesInDomains(directory, NSUserDomainMask, true)
                .mapNotNull { it as? String }
                .map { path -> KnownFolder(label, path) }
        }

        val tmpPath = NSTemporaryDirectory().takeIf { it.isNotBlank() }
            ?.let { listOf(KnownFolder("tmp", it)) }
            .orEmpty()

        return (standardFolders + tmpPath)
            .distinctBy { it.path }
            .sortedBy { it.path.count { ch -> ch == '/' } }
    }

    actual fun getParentPath(path: String): String? =
        NSURL.fileURLWithPath(path)
            .URLByDeletingLastPathComponent
            ?.path

    actual fun readFileText(path: String): String =
        NSString.stringWithContentsOfFile(path, NSUTF8StringEncoding, null) ?: ""

    actual fun exportFile(context: PlatformContext, path: String): String? {
        val docsDir = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, true)
            .firstOrNull() as? String ?: return null
        val destPath = "$docsDir/${path.substringAfterLast('/')}"
        return if (NSFileManager.defaultManager.copyItemAtPath(path, destPath, null)) {
            destPath
        } else {
            null
        }
    }
}
