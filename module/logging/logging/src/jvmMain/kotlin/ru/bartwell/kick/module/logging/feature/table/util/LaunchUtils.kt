package ru.bartwell.kick.module.logging.feature.table.util

import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.module.logging.core.persist.LogEntity
import ru.bartwell.kick.module.logging.feature.table.extension.toLogString
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.io.File
import javax.swing.JFileChooser
import javax.swing.SwingUtilities

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING", "OptionalUnit")
internal actual object LaunchUtils {
    internal actual fun canCopyLogs(): Boolean = true
    internal actual fun canSaveLogsToFile(): Boolean = true
    internal actual fun canShareLogsAsText(): Boolean = false
    internal actual fun canShareLogsAsFile(): Boolean = false

    internal actual fun copyLogs(context: PlatformContext, logs: List<LogEntity>) {
        val text = logs.joinToString(separator = "\n") { it.toLogString() }
        Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(text), null)
    }

    internal actual fun saveLogsToFile(context: PlatformContext, logs: List<LogEntity>) {
        SwingUtilities.invokeLater {
            val chooser = JFileChooser().apply {
                dialogTitle = "Save logs"
                selectedFile = File("desktop.log")
            }
            if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                writeLogsToFile(chooser.selectedFile, logs)
            }
        }
    }

    internal actual fun shareLogsAsText(context: PlatformContext, logs: List<LogEntity>) = Unit

    internal actual fun shareLogsAsFile(context: PlatformContext, logs: List<LogEntity>) = Unit

    private fun writeLogsToFile(file: File, logs: List<LogEntity>) {
        file.bufferedWriter().use { writer ->
            logs.forEach { item ->
                writer.write(item.toLogString())
                writer.newLine()
            }
        }
    }
}
