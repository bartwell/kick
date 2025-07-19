package ru.bartwell.kick.module.logging.feature.table.util

import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIPasteboard
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.module.logging.core.persist.LogEntity
import ru.bartwell.kick.module.logging.feature.table.extension.toLogString

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal actual object LaunchUtils {
    @OptIn(ExperimentalForeignApi::class)
    internal actual fun shareLogs(context: PlatformContext, logs: List<LogEntity>) {
        UIPasteboard.generalPasteboard.string = logs.joinToString { logEntity -> logEntity.toLogString() }
    }
}
