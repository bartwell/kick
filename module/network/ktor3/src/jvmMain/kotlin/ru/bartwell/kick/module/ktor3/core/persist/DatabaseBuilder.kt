package ru.bartwell.kick.module.ktor3.core.persist

import androidx.room.Room
import androidx.room.RoomDatabase
import ru.bartwell.kick.core.data.PlatformContext
import java.io.File

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal actual class DatabaseBuilder {
    actual fun createBuilder(context: PlatformContext): RoomDatabase.Builder<Ktor3Database> {
        val dbFile = File(System.getProperty("java.io.tmpdir"), "kick_ktor3.db")
        return Room.databaseBuilder<Ktor3Database>(name = dbFile.absolutePath)
    }
}
