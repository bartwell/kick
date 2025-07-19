package ru.bartwell.kick.module.logging.feature.table.util

import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.module.logging.core.persist.LogEntity
import ru.bartwell.kick.module.logging.feature.table.extension.toLogString
import java.io.File
import javax.swing.JFileChooser
import javax.swing.SwingUtilities

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal actual object LaunchUtils {
    internal actual fun shareLogs(context: PlatformContext, logs: List<LogEntity>) {
        SwingUtilities.invokeLater {
            val chooser = JFileChooser().apply {
                dialogTitle = "Save logs"
                selectedFile = File("desktop.log")
            }
            if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                val file = chooser.selectedFile
                file.bufferedWriter().use { writer ->
                    logs.forEach { item ->
                        writer.write(item.toLogString())
                        writer.newLine()
                    }
                }
            }
        }
    }
}
