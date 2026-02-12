package ru.bartwell.kick.module.firebase.analytics.core.persist

import ru.bartwell.kick.module.firebase.analytics.core.data.UserProperty
import ru.bartwell.kick.module.firebase.analytics.db.UserProperty as DbUserProperty

internal data class UserPropertyEntity(
    val name: String,
    val value: String,
    val timestamp: Long,
)

internal fun UserPropertyEntity.toDomain(): UserProperty = UserProperty(
    name = name,
    value = value,
    timestamp = timestamp,
)

internal fun UserProperty.toEntity(): UserPropertyEntity = UserPropertyEntity(
    name = name,
    value = value,
    timestamp = timestamp,
)

internal fun DbUserProperty.toEntity(): UserPropertyEntity = UserPropertyEntity(
    name = name,
    value = value_,
    timestamp = timestamp,
)
