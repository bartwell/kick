package ru.bartwell.kick.module.logging.core.persist

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.module.logging.core.persist.adapter.logLevelAdapter
import ru.bartwell.kick.module.logging.db.Log
import ru.bartwell.kick.module.logging.db.LoggingDb
import java.util.Properties

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal actual class DatabaseBuilder {
    actual fun createDatabase(context: PlatformContext): LoggingDatabase {
        val driver = JdbcSqliteDriver("jdbc:sqlite:kick_logging.db", Properties(), LoggingDb.Schema.synchronous())
        val db = LoggingDb(
            driver = driver,
            logAdapter = Log.Adapter(levelAdapter = logLevelAdapter)
        )
        return LoggingDatabase(db)
    }
}
