package ru.bartwell.kick.module.firebase.analytics.core.persist

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.bartwell.kick.module.firebase.analytics.core.data.UserId
import ru.bartwell.kick.module.firebase.analytics.db.FirebaseAnalyticsDb

internal class UserIdDao(private val db: FirebaseAnalyticsDb) {

    suspend fun insert(item: UserIdEntity) = withContext(Dispatchers.Default) {
        db.firebase_analyticsQueries.insertUserId(
            value_ = item.value,
            timestamp = item.timestamp,
        )
    }

    fun getLatestAsFlow(): Flow<UserId?> =
        db.firebase_analyticsQueries
            .selectLatestUserId()
            .asFlow()
            .mapToOneOrNull(Dispatchers.Default)
            .map { it?.toEntity()?.toDomain() }
}
