package ru.bartwell.kick.sample.shared.database.sqldelight

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.core.data.get
import ru.bartwell.kick.sample.shared.SampleDatabase

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class DriverFactory {
    actual fun createDriver(context: PlatformContext): SqlDriver {
        val driver = AndroidSqliteDriver(
            schema = SampleDatabase.Schema.synchronous(),
            context = context.get(),
            name = "sample.db",
        )
        try {
            SampleDatabase.Schema.synchronous().create(driver)
        } catch (_: RuntimeException) {}
        return driver
    }
}
