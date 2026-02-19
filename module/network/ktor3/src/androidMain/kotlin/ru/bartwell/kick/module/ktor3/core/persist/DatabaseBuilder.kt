package ru.bartwell.kick.module.ktor3.core.persist

import android.util.Log
import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.core.data.get
import ru.bartwell.kick.module.ktor3.core.persist.adapter.httpMethodAdapter
import ru.bartwell.kick.module.ktor3.db.Ktor3Db
import ru.bartwell.kick.module.ktor3.db.Request

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal actual class DatabaseBuilder {
    actual fun createDatabase(context: PlatformContext): Ktor3Database {
        val appContext = context.get().applicationContext
        val driver = AndroidSqliteDriver(
            schema = Ktor3Db.Schema.synchronous(),
            context = appContext,
            name = "kick_ktor3.db"
        )
        runCatching {
            Ktor3Db.Schema.synchronous().create(driver)
        }.onFailure { throwable ->
            Log.e("KickKtor3Db", "Failed to create ktor3 schema", throwable)
        }
        val db = Ktor3Db(
            driver = driver,
            requestAdapter = Request.Adapter(
                methodAdapter = httpMethodAdapter,
            )
        )
        return Ktor3Database(db)
    }
}
