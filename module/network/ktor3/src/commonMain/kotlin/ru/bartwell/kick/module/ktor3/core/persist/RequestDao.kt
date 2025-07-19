package ru.bartwell.kick.core.persist

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
public interface RequestDao {
    @Insert
    public suspend fun insert(item: RequestEntity)

    @Query("SELECT * FROM RequestEntity ORDER BY timestamp DESC")
    public fun getAllAsFlow(): Flow<List<RequestEntity>>

    @Query(
        "SELECT * FROM RequestEntity " +
            "WHERE " +
            "url LIKE :filter OR " +
            "method LIKE :filter OR " +
            "requestHeaders LIKE :filter OR " +
            "requestBody LIKE :filter OR " +
            "responseHeaders LIKE :filter OR " +
            "responseBody LIKE :filter " +
            "ORDER BY timestamp DESC"
    )
    public fun getFilteredAsFlow(filter: String): Flow<List<RequestEntity>>

    @Query("SELECT * FROM RequestEntity WHERE id = :id")
    public suspend fun getById(id: Long): RequestEntity?

    @Query("DELETE FROM RequestEntity")
    public suspend fun deleteAll()

    @Query("DELETE FROM RequestEntity WHERE timestamp < :timestamp")
    public suspend fun deleteOld(timestamp: Long)
}
