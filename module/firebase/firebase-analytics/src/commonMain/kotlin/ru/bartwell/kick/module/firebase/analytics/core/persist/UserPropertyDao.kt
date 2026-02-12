package ru.bartwell.kick.module.firebase.analytics.core.persist

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.bartwell.kick.module.firebase.analytics.core.data.UserProperty
import ru.bartwell.kick.module.firebase.analytics.db.FirebaseAnalyticsDb

internal class UserPropertyDao(private val db: FirebaseAnalyticsDb) {

    suspend fun upsert(item: UserPropertyEntity) = withContext(Dispatchers.Default) {
        db.firebase_analyticsQueries.insertProperty(
            name = item.name,
            value_ = item.value,
            timestamp = item.timestamp,
        )
    }

    fun getAllAsFlow(): Flow<List<UserProperty>> =
        db.firebase_analyticsQueries
            .selectProperties()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { list -> list.map { it.toEntity().toDomain() } }

    suspend fun deleteAll() = withContext(Dispatchers.Default) {
        db.firebase_analyticsQueries.deleteProperties()
    }
}
