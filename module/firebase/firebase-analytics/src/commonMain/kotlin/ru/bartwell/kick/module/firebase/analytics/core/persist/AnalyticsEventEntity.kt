package ru.bartwell.kick.module.firebase.analytics.core.persist

import ru.bartwell.kick.module.firebase.analytics.core.data.AnalyticsEvent
import ru.bartwell.kick.module.firebase.analytics.db.AnalyticsEvent as DbAnalyticsEvent

internal data class AnalyticsEventEntity(
    val id: Long = 0,
    val timestamp: Long,
    val name: String,
    val params: Map<String, String> = emptyMap(),
)

internal fun AnalyticsEventEntity.toDomain(): AnalyticsEvent = AnalyticsEvent(
    id = id,
    timestamp = timestamp,
    name = name,
    params = params,
)

internal fun AnalyticsEvent.toEntity(): AnalyticsEventEntity = AnalyticsEventEntity(
    id = id,
    timestamp = timestamp,
    name = name,
    params = params,
)

internal fun DbAnalyticsEvent.toEntity(): AnalyticsEventEntity = AnalyticsEventEntity(
    id = id,
    timestamp = timestamp,
    name = name,
    params = params,
)
