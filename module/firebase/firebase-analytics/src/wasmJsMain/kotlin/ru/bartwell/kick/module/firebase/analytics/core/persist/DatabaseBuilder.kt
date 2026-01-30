package ru.bartwell.kick.module.firebase.analytics.core.persist

import app.cash.sqldelight.async.coroutines.awaitCreate
import app.cash.sqldelight.driver.worker.createDefaultWebWorkerDriver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.module.firebase.analytics.core.persist.adapter.stringMapAdapter
import ru.bartwell.kick.module.firebase.analytics.db.AnalyticsEvent
import ru.bartwell.kick.module.firebase.analytics.db.FirebaseAnalyticsDb

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal actual class DatabaseBuilder {
    actual fun createDatabase(context: PlatformContext): FirebaseAnalyticsDatabase {
        val driver = createDefaultWebWorkerDriver()
        CoroutineScope(Dispatchers.Default).launch {
            FirebaseAnalyticsDb.Schema.awaitCreate(driver)
        }
        val db = FirebaseAnalyticsDb(
            driver = driver,
            analyticsEventAdapter = AnalyticsEvent.Adapter(
                paramsAdapter = stringMapAdapter,
            )
        )
        return FirebaseAnalyticsDatabase(db)
    }
}
