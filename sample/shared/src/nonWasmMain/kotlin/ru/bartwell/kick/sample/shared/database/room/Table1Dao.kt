package ru.bartwell.kick.sample.shared.database.room

import androidx.room.Dao
import androidx.room.Query

@Dao
interface Table1Dao {
    @Query("SELECT * FROM table1")
    suspend fun getAll(): List<Table1Entity>
}
