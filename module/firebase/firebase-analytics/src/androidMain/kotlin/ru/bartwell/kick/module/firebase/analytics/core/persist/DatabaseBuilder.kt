package ru.bartwell.kick.module.firebase.analytics.core.persist

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.core.data.get
import ru.bartwell.kick.module.firebase.analytics.core.persist.adapter.stringMapAdapter
import ru.bartwell.kick.module.firebase.analytics.db.AnalyticsEvent
import ru.bartwell.kick.module.firebase.analytics.db.FirebaseAnalyticsDb

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal actual class DatabaseBuilder {
    actual fun createDatabase(context: PlatformContext): FirebaseAnalyticsDatabase {
        val appContext = context.get().applicationContext
        val driver = AndroidSqliteDriver(
            schema = FirebaseAnalyticsDb.Schema.synchronous(),
            context = appContext,
            name = "kick_firebase_analytics.db"
        )
        try {
            FirebaseAnalyticsDb.Schema.synchronous().create(driver)
        } catch (_: RuntimeException) {}
        val db = FirebaseAnalyticsDb(
            driver = driver,
            analyticsEventAdapter = AnalyticsEvent.Adapter(
                paramsAdapter = stringMapAdapter,
            )
        )
        return FirebaseAnalyticsDatabase(db)
    }
}
