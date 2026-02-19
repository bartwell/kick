package ru.bartwell.kick.module.logging.feature.table.util

import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.module.logging.core.persist.LogEntity

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal expect object LaunchUtils {
    internal fun canCopyLogs(): Boolean
    internal fun canSaveLogsToFile(): Boolean
    internal fun canShareLogsAsText(): Boolean
    internal fun canShareLogsAsFile(): Boolean

    internal fun copyLogs(context: PlatformContext, logs: List<LogEntity>)
    internal fun saveLogsToFile(context: PlatformContext, logs: List<LogEntity>)
    internal fun shareLogsAsText(context: PlatformContext, logs: List<LogEntity>)
    internal fun shareLogsAsFile(context: PlatformContext, logs: List<LogEntity>)
}
