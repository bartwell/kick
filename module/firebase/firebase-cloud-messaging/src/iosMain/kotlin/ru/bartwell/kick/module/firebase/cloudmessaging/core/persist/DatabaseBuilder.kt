package ru.bartwell.kick.module.firebase.cloudmessaging.core.persist

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.module.firebase.cloudmessaging.core.persist.adapter.stringMapAdapter
import ru.bartwell.kick.module.firebase.cloudmessaging.db.FcmMessage
import ru.bartwell.kick.module.firebase.cloudmessaging.db.FirebaseCloudMessagingDb

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal actual class DatabaseBuilder {
    actual fun createDatabase(context: PlatformContext): FirebaseCloudMessagingDatabase {
        val driver = NativeSqliteDriver(
            schema = FirebaseCloudMessagingDb.Schema.synchronous(),
            name = "kick_firebase_cloud_messaging.db",
        )
        val db = FirebaseCloudMessagingDb(
            driver = driver,
            fcmMessageAdapter = FcmMessage.Adapter(
                dataPayloadAdapter = stringMapAdapter,
                rawPayloadAdapter = stringMapAdapter,
            ),
        )
        return FirebaseCloudMessagingDatabase(db)
    }
}
