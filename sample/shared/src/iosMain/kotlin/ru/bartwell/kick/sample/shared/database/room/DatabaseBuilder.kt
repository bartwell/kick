package ru.bartwell.kick.sample.shared.database.room

import androidx.room.Room
import androidx.room.RoomDatabase
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.core.util.PathUtils

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class DatabaseBuilder {
    actual fun createBuilder(context: PlatformContext): RoomDatabase.Builder<AppDatabase> {
        val dbFilePath = PathUtils.documentDirectory() + "/sample_room.db"
        return Room.databaseBuilder<AppDatabase>(name = dbFilePath)
    }
}
