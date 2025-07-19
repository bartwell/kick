package ru.bartwell.kick.module.logging.core.persist

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor

@Database(entities = [LogEntity::class], version = 1)
@ConstructedBy(LoggingDatabaseConstructor::class)
internal abstract class LoggingDatabase : RoomDatabase() {
    abstract fun getLogDao(): LogDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
internal expect object LoggingDatabaseConstructor : RoomDatabaseConstructor<LoggingDatabase> {
    override fun initialize(): LoggingDatabase
}
