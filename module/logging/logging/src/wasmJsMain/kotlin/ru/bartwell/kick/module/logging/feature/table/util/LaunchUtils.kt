package ru.bartwell.kick.module.logging.feature.table.util

import kotlinx.browser.window
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.module.logging.core.persist.LogEntity
import ru.bartwell.kick.module.logging.feature.table.extension.toLogString

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal actual object LaunchUtils {
    internal actual fun shareLogs(context: PlatformContext, logs: List<LogEntity>) {
        val text = logs.joinToString(separator = "\n") { it.toLogString() }
        try {
            val clipboard = window.navigator.clipboard
            clipboard?.writeText(text)
        } catch (_: Throwable) {
            kotlin.io.println(text)
        }
    }
}
