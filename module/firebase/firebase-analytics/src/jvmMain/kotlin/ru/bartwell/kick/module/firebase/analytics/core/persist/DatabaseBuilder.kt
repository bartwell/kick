package ru.bartwell.kick.module.firebase.analytics.core.persist

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.module.firebase.analytics.core.persist.adapter.stringMapAdapter
import ru.bartwell.kick.module.firebase.analytics.db.AnalyticsEvent
import ru.bartwell.kick.module.firebase.analytics.db.FirebaseAnalyticsDb
import java.util.Properties

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal actual class DatabaseBuilder {
    actual fun createDatabase(context: PlatformContext): FirebaseAnalyticsDatabase {
        val driver = JdbcSqliteDriver("jdbc:sqlite:kick_firebase_analytics.db", Properties(), FirebaseAnalyticsDb.Schema.synchronous())
        val db = FirebaseAnalyticsDb(
            driver = driver,
            analyticsEventAdapter = AnalyticsEvent.Adapter(
                paramsAdapter = stringMapAdapter,
            )
        )
        return FirebaseAnalyticsDatabase(db)
    }
}
