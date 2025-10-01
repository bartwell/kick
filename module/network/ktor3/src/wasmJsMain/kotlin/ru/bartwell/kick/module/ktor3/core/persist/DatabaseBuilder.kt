package ru.bartwell.kick.module.ktor3.core.persist

import app.cash.sqldelight.async.coroutines.awaitCreate
import app.cash.sqldelight.driver.worker.createDefaultWebWorkerDriver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.module.ktor3.core.persist.adapter.httpMethodAdapter
import ru.bartwell.kick.module.ktor3.db.Ktor3Db
import ru.bartwell.kick.module.ktor3.db.Request

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal actual class DatabaseBuilder {
    actual fun createDatabase(context: PlatformContext): Ktor3Database {
        val driver = createDefaultWebWorkerDriver()
        CoroutineScope(Dispatchers.Default).launch {
            Ktor3Db.Schema.awaitCreate(driver)
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
