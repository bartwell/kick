package ru.bartwell.kick.module.logging.core.persist

import ru.bartwell.kick.core.data.PlatformContext

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal expect class DatabaseBuilder() {
    fun createDatabase(context: PlatformContext): LoggingDatabase
}
