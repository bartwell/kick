package ru.bartwell.kick.module.logging.feature.table.util

import kotlinx.browser.window
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.module.logging.core.persist.LogEntity
import ru.bartwell.kick.module.logging.feature.table.extension.toLogString

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING", "OptionalUnit")
internal actual object LaunchUtils {
    internal actual fun canCopyLogs(): Boolean = true
    internal actual fun canSaveLogsToFile(): Boolean = false
    internal actual fun canShareLogsAsText(): Boolean = false
    internal actual fun canShareLogsAsFile(): Boolean = false

    internal actual fun copyLogs(context: PlatformContext, logs: List<LogEntity>) {
        val text = logs.joinToString(separator = "\n") { it.toLogString() }
        try {
            val clipboard = window.navigator.clipboard
            clipboard?.writeText(text)
        } catch (_: Throwable) {
            kotlin.io.println(text)
        }
    }

    internal actual fun saveLogsToFile(context: PlatformContext, logs: List<LogEntity>) = Unit

    internal actual fun shareLogsAsText(context: PlatformContext, logs: List<LogEntity>) = Unit

    internal actual fun shareLogsAsFile(context: PlatformContext, logs: List<LogEntity>) = Unit
}
