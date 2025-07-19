package ru.bartwell.kick.module.logging.core.persist

import androidx.room.RoomDatabase
import ru.bartwell.kick.core.data.PlatformContext

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal expect class DatabaseBuilder() {
    fun createBuilder(context: PlatformContext): RoomDatabase.Builder<LoggingDatabase>
}
