package ru.bartwell.kick.sample.shared.database.room

import androidx.room.RoomDatabase
import ru.bartwell.kick.core.data.PlatformContext

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class DatabaseBuilder() {
    fun createBuilder(context: PlatformContext): RoomDatabase.Builder<AppDatabase>
}
