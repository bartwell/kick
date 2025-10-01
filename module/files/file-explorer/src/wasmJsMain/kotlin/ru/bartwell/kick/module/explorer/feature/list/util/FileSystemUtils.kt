package ru.bartwell.kick.module.explorer.feature.list.util

import kotlinx.coroutines.await
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.module.explorer.feature.list.data.FileEntry
import ru.bartwell.kick.module.explorer.feature.list.data.Result
import kotlin.js.JsAny
import kotlin.js.Promise

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal actual object FileSystemUtils {

    actual suspend fun getInitialDirectory(context: PlatformContext): String = "/"

    actual suspend fun listDirectory(path: String): List<FileEntry> {
        val json = jsListDirByPath(path).await<JsAny?>().toString()
        val items = Json.decodeFromString<List<EntryDto>>(json)
        return items.map { e ->
            val isDir = e.kind == "directory"
            val size = e.size?.toLong()
            val lastModified = e.lastModified?.toLong() ?: 0L
            FileEntry(name = e.name, isDirectory = isDir, size = size, lastModified = lastModified)
        }.sortedWith(compareByDescending<FileEntry> { it.isDirectory }.thenBy { it.name })
    }

    actual suspend fun getKnownFolders(context: PlatformContext): List<KnownFolder> =
        listOf(KnownFolder(name = "OPFS Root", path = "/"))

    actual fun getParentPath(path: String): String? {
        val normalized = path.trim().trimEnd('/')
        if (normalized.isEmpty() || normalized == "/") return null
        val lastSlash = normalized.lastIndexOf('/')
        return if (lastSlash <= 0) "/" else normalized.substring(0, lastSlash)
    }

    @Suppress("TooGenericExceptionCaught")
    actual suspend fun readText(path: String): Result = try {
        val text = jsReadTextByPath(path).await<JsAny?>().toString()
        Result.Success(text)
    } catch (t: Throwable) {
        Result.Error(t.message ?: t.toString())
    }

    @Suppress("TooGenericExceptionCaught")
    actual suspend fun exportFile(context: PlatformContext, path: String): Result = try {
        val ok = jsDownloadByPath(path).await<JsAny?>().toString() == "true"
        if (ok) Result.Success("Downloaded: ${path.substringAfterLast('/')} ") else Result.Error("Download failed")
    } catch (t: Throwable) {
        Result.Error(t.message ?: t.toString())
    }

    @Suppress("TooGenericExceptionCaught")
    actual suspend fun deleteFile(path: String): Result = try {
        val ok = jsDeleteByPath(path).await<JsAny?>().toString() == "true"
        if (ok) Result.Success(path) else Result.Error("Delete failed")
    } catch (t: Throwable) {
        Result.Error(t.message ?: t.toString())
    }
}

@Serializable
private data class EntryDto(
    val name: String,
    val kind: String,
    val size: Double? = null,
    @SerialName("lastModified") val lastModified: Double? = null,
)

@JsFun(
    "(path) => (async () => {\n" +
        "  const root = await navigator.storage.getDirectory();\n" +
        "  const clean = (path||'').trim();\n" +
        "  const parts = (clean==='/'||clean==='') ? [] : clean.split('/').filter(p=>p.length>0);\n" +
        "  let dir = root;\n" +
        "  for (const p of parts) { dir = await dir.getDirectoryHandle(p); }\n" +
        "  const out = [];\n" +
        "  for await (const [name, handle] of dir.entries()) {\n" +
        "    let size=null, lastModified=null;\n" +
        "    if (handle.kind === 'file') {\n" +
        "      try { const f = await handle.getFile();\n" +
        "            size = f.size;\n" +
        "            lastModified = f.lastModified;\n" +
        "      } catch(e){}\n" +
        "    }\n" +
        "    out.push({name, kind: handle.kind, size, lastModified});\n" +
        "  }\n" +
        "  return JSON.stringify(out);\n" +
        "})()"
)
private external fun jsListDirByPath(path: String): Promise<JsAny?>

@JsFun(
    "(path) => (async () => {\n" +
        "  const root = await navigator.storage.getDirectory();\n" +
        "  const parts = (path||'').split('/').filter(p=>p.length>0);\n" +
        "  if (parts.length===0) throw 'Invalid path';\n" +
        "  const name = parts.pop();\n" +
        "  let dir = root;\n" +
        "  for (const p of parts) { dir = await dir.getDirectoryHandle(p); }\n" +
        "  const fh = await dir.getFileHandle(name);\n" +
        "  const file = await fh.getFile();\n" +
        "  return await file.text();\n" +
        "})()"
)
private external fun jsReadTextByPath(path: String): Promise<JsAny?>

@JsFun(
    "(path) => (async () => {\n" +
        "  try {\n" +
        "    const root = await navigator.storage.getDirectory();\n" +
        "    const parts = (path||'').split('/').filter(p=>p.length>0);\n" +
        "    if (parts.length===0) return false;\n" +
        "    const name = parts.pop();\n" +
        "    let dir = root;\n" +
        "    for (const p of parts) { dir = await dir.getDirectoryHandle(p); }\n" +
        "    await dir.removeEntry(name, { recursive: false });\n" +
        "    return true;\n" +
        "  } catch(e) { return false; }\n" +
        "})()"
)
private external fun jsDeleteByPath(path: String): Promise<JsAny?>

@JsFun(
    "(path) => (async () => {\n" +
        "  try {\n" +
        "    const root = await navigator.storage.getDirectory();\n" +
        "    const parts = (path||'').split('/').filter(p=>p.length>0);\n" +
        "    if (parts.length===0) return false;\n" +
        "    const name = parts.pop();\n" +
        "    let dir = root;\n" +
        "    for (const p of parts) { dir = await dir.getDirectoryHandle(p); }\n" +
        "    const fh = await dir.getFileHandle(name);\n" +
        "    const file = await fh.getFile();\n" +
        "    const url = URL.createObjectURL(file);\n" +
        "    const a = document.createElement('a'); a.href = url; a.download = name;\n" +
        "    (document.body||document.documentElement).appendChild(a);\n" +
        "    a.click(); a.remove();\n" +
        "    setTimeout(()=>URL.revokeObjectURL(url),0);\n" +
        "    return true;\n" +
        "  } catch(e) { return false; }\n" +
        "})()"
)
private external fun jsDownloadByPath(path: String): Promise<JsAny?>
