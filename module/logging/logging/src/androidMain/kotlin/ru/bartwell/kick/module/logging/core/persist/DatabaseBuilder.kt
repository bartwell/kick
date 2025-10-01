package ru.bartwell.kick.module.logging.core.persist

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.core.data.get
import ru.bartwell.kick.module.logging.core.persist.adapter.logLevelAdapter
import ru.bartwell.kick.module.logging.db.Log
import ru.bartwell.kick.module.logging.db.LoggingDb

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal actual class DatabaseBuilder {
    actual fun createDatabase(context: PlatformContext): LoggingDatabase {
        val appContext = context.get().applicationContext
        val driver = AndroidSqliteDriver(
            schema = LoggingDb.Schema.synchronous(),
            context = appContext,
            name = "kick_logging.db"
        )
        try {
            LoggingDb.Schema.synchronous().create(driver)
        } catch (_: RuntimeException) {}
        val db = LoggingDb(
            driver = driver,
            logAdapter = Log.Adapter(levelAdapter = logLevelAdapter)
        )
        return LoggingDatabase(db)
    }
}
