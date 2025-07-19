package ru.bartwell.kick.module.logging.feature.table.util

import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.module.logging.core.persist.LogEntity

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal expect object LaunchUtils {
    internal fun shareLogs(context: PlatformContext, logs: List<LogEntity>)
}
