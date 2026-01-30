package ru.bartwell.kick.module.firebase.analytics.core.persist

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.bartwell.kick.module.firebase.analytics.db.FirebaseAnalyticsDb
import ru.bartwell.kick.module.firebase.analytics.core.data.AnalyticsEvent

internal class AnalyticsEventDao(private val db: FirebaseAnalyticsDb) {

    suspend fun insert(item: AnalyticsEventEntity) = withContext(Dispatchers.Default) {
        db.firebase_analyticsQueries.insertEvent(
            timestamp = item.timestamp,
            name = item.name,
            params = item.params,
        )
    }

    fun getAllAsFlow(): Flow<List<AnalyticsEvent>> =
        db.firebase_analyticsQueries
            .selectEvents()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { list -> list.map { it.toEntity().toDomain() } }

    suspend fun deleteAll() = withContext(Dispatchers.Default) {
        db.firebase_analyticsQueries.deleteEvents()
    }
}
