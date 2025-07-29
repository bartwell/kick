package ru.bartwell.kick.module.explorer.feature.list.util

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.get
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.value
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSData
import platform.Foundation.NSDataReadingMappedIfSafe
import platform.Foundation.NSDate
import platform.Foundation.NSDirectoryEnumerationSkipsHiddenFiles
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSError
import platform.Foundation.NSFileManager
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
import platform.Foundation.dataWithContentsOfFile
import platform.Foundation.timeIntervalSince1970
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.module.explorer.feature.list.data.FileEntry
import ru.bartwell.kick.module.explorer.feature.list.data.Result

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

    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    actual fun readText(path: String): Result = memScoped {
        val errorVar = alloc<ObjCObjectVar<NSError?>>()

        val data = NSData.dataWithContentsOfFile(
            path = path,
            options = NSDataReadingMappedIfSafe,
            error = errorVar.ptr
        )

        errorVar.value?.let { err ->
            return@memScoped Result.Error("Can't read file '$path': ${err.localizedDescription}")
        }

        val result = data?.let {
            val length = data.length.toInt()
            val ptr = data.bytes!!.reinterpret<ByteVar>()
            val byteArray = ByteArray(length) { i -> ptr[i] }
            byteArray.decodeToString()
        } ?: ""

        Result.Success(result)
    }

    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    actual fun exportFile(context: PlatformContext, path: String): Result = memScoped {
        val dirs = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, true)
        val targetDir = dirs.firstOrNull() as? String
            ?: run {
                return@memScoped Result.Error("Can't locate Documents directory")
            }
        val fileName = NSURL.fileURLWithPath(path).lastPathComponent ?: run {
            return@memScoped Result.Error("Can't extract file name from '$path'")
        }
        val destPath = "$targetDir/$fileName"
        val errorVar = alloc<ObjCObjectVar<NSError?>>()
        val success = NSFileManager.defaultManager.copyItemAtPath(
            path,
            toPath = destPath,
            error = errorVar.ptr
        )
        if (!success) {
            val message = errorVar.value?.localizedDescription ?: "Unknown error"
            return@memScoped Result.Error("Can't copy file '$path' to '$destPath': $message")
        }
        Result.Success(destPath)
    }
}
