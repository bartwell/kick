package ru.bartwell.kick.module.logging.core.persist

import androidx.room.Room
import androidx.room.RoomDatabase
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.core.data.get

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal actual class DatabaseBuilder {
    actual fun createBuilder(context: PlatformContext): RoomDatabase.Builder<LoggingDatabase> {
        val appContext = context.get().applicationContext
        val dbFile = appContext.getDatabasePath("kick_logging.db")
        return Room.databaseBuilder<LoggingDatabase>(context = appContext, name = dbFile.absolutePath)
    }
}
