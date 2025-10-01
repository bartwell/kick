package ru.bartwell.kick.module.logging.core.persist

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.module.logging.core.persist.adapter.logLevelAdapter
import ru.bartwell.kick.module.logging.db.Log
import ru.bartwell.kick.module.logging.db.LoggingDb

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal actual class DatabaseBuilder {
    actual fun createDatabase(context: PlatformContext): LoggingDatabase {
        val driver = NativeSqliteDriver(schema = LoggingDb.Schema.synchronous(), name = "kick_logging.db")
        val db = LoggingDb(
            driver = driver,
            logAdapter = Log.Adapter(levelAdapter = logLevelAdapter)
        )
        return LoggingDatabase(db)
    }
}
