package ru.bartwell.kick.sample.shared.database.room

import androidx.room.Dao
import androidx.room.Query

@Dao
interface Table2Dao {
    @Query("SELECT * FROM table2")
    suspend fun getAll(): List<Table2Entity>
}
