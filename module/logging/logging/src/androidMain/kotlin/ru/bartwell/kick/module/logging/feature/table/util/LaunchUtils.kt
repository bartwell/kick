package ru.bartwell.kick.module.logging.feature.table.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.core.data.get
import ru.bartwell.kick.module.logging.core.persist.LogEntity
import ru.bartwell.kick.module.logging.feature.table.extension.toLogString
import java.io.File

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal actual object LaunchUtils {
    private const val LOG_MIME_TYPE = "text/plain"
    private const val LOGS_FILE_NAME = "logs.txt"
    private const val SHARE_FILE_NAME = "android.log"
    private const val SHARE_TEXT_TITLE = "Share logs as text"
    private const val SHARE_FILE_TITLE = "Share logs as file"

    internal actual fun canCopyLogs(): Boolean = true
    internal actual fun canSaveLogsToFile(): Boolean = true
    internal actual fun canShareLogsAsText(): Boolean = true
    internal actual fun canShareLogsAsFile(): Boolean = true

    internal actual fun copyLogs(context: PlatformContext, logs: List<LogEntity>) {
        val androidContext = context.get()
        val text = logs.joinToString(separator = "\n") { it.toLogString() }
        val manager = androidContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        manager.setPrimaryClip(ClipData.newPlainText("logs", text))
    }

    internal actual fun saveLogsToFile(context: PlatformContext, logs: List<LogEntity>) {
        val activity = context.get() as? ComponentActivity ?: return
        val text = logs.joinToString(separator = "\n") { it.toLogString() }
        val key = "save_logs_${System.nanoTime()}"

        lateinit var launcher: ActivityResultLauncher<String>
        launcher = activity.activityResultRegistry.register(
            key,
            ActivityResultContracts.CreateDocument(LOG_MIME_TYPE)
        ) { uri ->
            uri?.let {
                activity.contentResolver.openOutputStream(it)?.bufferedWriter()?.use { writer ->
                    writer.write(text)
                }
            }
            launcher.unregister()
        }
        launcher.launch(LOGS_FILE_NAME)
    }

    internal actual fun shareLogsAsText(context: PlatformContext, logs: List<LogEntity>) {
        val androidContext = context.get()
        val text = logs.joinToString(separator = "\n") { it.toLogString() }
        Intent(Intent.ACTION_SEND).apply {
            type = LOG_MIME_TYPE
            putExtra(Intent.EXTRA_TEXT, text)
        }.also { intent ->
            androidContext.startActivity(Intent.createChooser(intent, SHARE_TEXT_TITLE))
        }
    }

    internal actual fun shareLogsAsFile(context: PlatformContext, logs: List<LogEntity>) {
        val androidContext = context.get()
        val file = writeLogsToFile(androidContext.filesDir, SHARE_FILE_NAME, logs)
        val uri = FileProvider.getUriForFile(
            androidContext,
            "${androidContext.packageName}.kickfileprovider",
            file
        )
        Intent(Intent.ACTION_SEND).apply {
            type = LOG_MIME_TYPE
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }.also { intent ->
            androidContext.startActivity(Intent.createChooser(intent, SHARE_FILE_TITLE))
        }
    }

    private fun writeLogsToFile(directory: File, fileName: String, logs: List<LogEntity>): File {
        val file = File(directory, fileName)
        file.bufferedWriter().use { writer ->
            logs.forEach { item ->
                writer.appendLine(item.toLogString())
            }
        }
        return file
    }
}
