package ru.bartwell.kick.sample.shared.database.sqldelight

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import ru.bartwell.kick.core.data.PlatformContext
import java.util.Properties

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class DriverFactory {
    actual fun createDriver(context: PlatformContext): SqlDriver {
        return JdbcSqliteDriver("jdbc:sqlite:sample.db", Properties())
    }
}
