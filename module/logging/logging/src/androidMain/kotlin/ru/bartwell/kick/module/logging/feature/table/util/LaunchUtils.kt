package ru.bartwell.kick.module.logging.feature.table.util

import android.content.Intent
import androidx.core.content.FileProvider
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.core.data.get
import ru.bartwell.kick.module.logging.core.persist.LogEntity
import ru.bartwell.kick.module.logging.feature.table.extension.toLogString
import java.io.File

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal actual object LaunchUtils {
    internal actual fun shareLogs(context: PlatformContext, logs: List<LogEntity>) {
        val androidContext = context.get()
        val fileName = "android.log"
        val file = File(androidContext.filesDir, fileName)
        file.bufferedWriter().use { writer ->
            logs.forEach { item ->
                writer.appendLine(item.toLogString())
            }
        }
        val uri = FileProvider.getUriForFile(
            androidContext,
            "${androidContext.packageName}.kickfileprovider",
            file
        )
        Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }.also { intent ->
            androidContext.startActivity(Intent.createChooser(intent, "Share logs"))
        }
    }
}
