package ru.bartwell.kick.sample.shared.database.sqldelight

import app.cash.sqldelight.async.coroutines.awaitCreate
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.worker.createDefaultWebWorkerDriver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.sample.shared.SampleDatabase

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class DriverFactory {
    actual fun createDriver(context: PlatformContext): SqlDriver {
        val driver = createDefaultWebWorkerDriver()
        CoroutineScope(Dispatchers.Default).launch {
            SampleDatabase.Schema.awaitCreate(driver)
        }
        return driver
    }
}
