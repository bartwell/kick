package ru.bartwell.kick.module.ktor3.core.persist

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import ru.bartwell.kick.core.persist.RequestDao
import ru.bartwell.kick.core.persist.RequestEntity

@Database(entities = [RequestEntity::class], version = 1)
@ConstructedBy(Ktor3DatabaseConstructor::class)
internal abstract class Ktor3Database : RoomDatabase() {
    abstract fun getRequestDao(): RequestDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
internal expect object Ktor3DatabaseConstructor : RoomDatabaseConstructor<Ktor3Database> {
    override fun initialize(): Ktor3Database
}
