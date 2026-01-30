package ru.bartwell.kick.module.firebase.analytics.core.persist

import ru.bartwell.kick.module.firebase.analytics.core.data.UserId
import ru.bartwell.kick.module.firebase.analytics.db.UserId as DbUserId

internal data class UserIdEntity(
    val id: Long = 0,
    val value: String?,
    val timestamp: Long,
)

internal fun UserIdEntity.toDomain(): UserId = UserId(
    value = value,
    timestamp = timestamp,
)

internal fun UserId.toEntity(): UserIdEntity = UserIdEntity(
    value = value,
    timestamp = timestamp,
)

internal fun DbUserId.toEntity(): UserIdEntity = UserIdEntity(
    id = id,
    value = value_,
    timestamp = timestamp,
)
