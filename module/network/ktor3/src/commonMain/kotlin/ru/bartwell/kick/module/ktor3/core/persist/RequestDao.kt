package ru.bartwell.kick.module.ktor3.core.persist

import app.cash.sqldelight.async.coroutines.awaitAsOneOrNull
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.bartwell.kick.module.ktor3.db.Ktor3Db
import ru.bartwell.kick.module.ktor3.db.Request

public class RequestDao(private val db: Ktor3Db) {

    public suspend fun insert(item: RequestEntity) {
        withContext<Unit>(Dispatchers.Default) {
            db.requestQueries.insertRequest(
                timestamp = item.timestamp,
                method = item.method,
                url = item.url,
                statusCode = item.statusCode?.toLong(),
                durationMs = item.durationMs,
                responseSizeBytes = item.responseSizeBytes,
                isSecure = item.isSecure,
                error = item.error,
                requestHeaders = item.requestHeaders,
                requestBody = item.requestBody,
                responseHeaders = item.responseHeaders,
                responseBody = item.responseBody,
            )
        }
    }

    public fun getAllAsFlow(): Flow<List<RequestEntity>> =
        db.requestQueries
            .selectAll()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { list -> list.map { it.toEntity() } }

    public fun getFilteredAsFlow(filter: String): Flow<List<RequestEntity>> =
        db.requestQueries
            .selectFiltered(filter)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { list -> list.map { it.toEntity() } }

    public suspend fun getById(id: Long): RequestEntity? = withContext(Dispatchers.Default) {
        db.requestQueries
            .selectById(id)
            .awaitAsOneOrNull()
            ?.toEntity()
    }

    public suspend fun deleteAll() {
        withContext(Dispatchers.Default) {
            db.requestQueries.deleteAll()
        }
    }

    public suspend fun deleteOld(timestamp: Long) {
        withContext(Dispatchers.Default) {
            db.requestQueries.deleteOld(timestamp)
        }
    }
}

private fun Request.toEntity(): RequestEntity = RequestEntity(
    id = id,
    timestamp = timestamp,
    method = method,
    url = url,
    statusCode = statusCode?.toInt(),
    durationMs = durationMs,
    responseSizeBytes = responseSizeBytes,
    isSecure = isSecure,
    error = error,
    requestHeaders = requestHeaders,
    requestBody = requestBody,
    responseHeaders = responseHeaders,
    responseBody = responseBody,
)
