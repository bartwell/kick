package ru.bartwell.kick.sample.shared.database.sqldelight

import app.cash.sqldelight.db.SqlDriver
import ru.bartwell.kick.core.data.PlatformContext

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class DriverFactory() {
    fun createDriver(context: PlatformContext): SqlDriver
}
