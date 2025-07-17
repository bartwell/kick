package ru.bartwell.kick.sample.shared.database.sqldelight

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.sample.shared.SampleDatabase

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class DriverFactory {
    actual fun createDriver(context: PlatformContext): SqlDriver {
        return NativeSqliteDriver(SampleDatabase.Schema, "sample.db")
    }
}
