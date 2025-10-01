package ru.bartwell.kick.module.logging.core.persist

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.bartwell.kick.module.logging.db.Log
import ru.bartwell.kick.module.logging.db.LoggingDb

internal class LogDao(private val db: LoggingDb) {

    suspend fun insert(item: LogEntity) = withContext(Dispatchers.Default) {
        db.logQueries.insertLog(
            time = item.time,
            level = item.level,
            message = item.message,
        )
    }

    fun getAllAsFlow(): Flow<List<LogEntity>> =
        db.logQueries
            .selectAll()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .let { flow -> flow.map { list -> list.map { it.toEntity() } } }

    suspend fun deleteAll() = withContext(Dispatchers.Default) {
        db.logQueries.deleteAll()
    }

    suspend fun deleteOld(time: Long) = withContext(Dispatchers.Default) {
        db.logQueries.deleteOld(time)
    }
}

private fun Log.toEntity(): LogEntity = LogEntity(
    id = id,
    time = time,
    level = level,
    message = message,
)
