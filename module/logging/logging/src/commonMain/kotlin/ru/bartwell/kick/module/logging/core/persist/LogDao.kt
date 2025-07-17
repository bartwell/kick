package ru.bartwell.kick.module.logging.core.persist

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
internal interface LogDao {
    @Insert
    suspend fun insert(item: LogEntity)

    @Query("SELECT * FROM LogEntity ORDER BY time DESC")
    fun getAllAsFlow(): Flow<List<LogEntity>>

    @Query("SELECT * FROM LogEntity WHERE message LIKE :filter ORDER BY time DESC")
    fun getFilteredAsFlow(filter: String): Flow<List<LogEntity>>

    @Query("DELETE FROM LogEntity")
    suspend fun deleteAll()

    @Query("DELETE FROM LogEntity WHERE time < :time")
    suspend fun deleteOld(time: Long)
}
