package ru.bartwell.kick.module.firebase.analytics.core.persist

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.module.firebase.analytics.core.persist.adapter.stringMapAdapter
import ru.bartwell.kick.module.firebase.analytics.db.AnalyticsEvent
import ru.bartwell.kick.module.firebase.analytics.db.FirebaseAnalyticsDb

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal actual class DatabaseBuilder {
    actual fun createDatabase(context: PlatformContext): FirebaseAnalyticsDatabase {
        val driver =
            NativeSqliteDriver(schema = FirebaseAnalyticsDb.Schema.synchronous(), name = "kick_firebase_analytics.db")
        val db = FirebaseAnalyticsDb(
            driver = driver,
            analyticsEventAdapter = AnalyticsEvent.Adapter(
                paramsAdapter = stringMapAdapter,
            )
        )
        return FirebaseAnalyticsDatabase(db)
    }
}
