package ru.bartwell.kick.module.firebase.analytics.core.data

import kotlinx.serialization.Serializable

@Serializable
internal data class UserId(
    val value: String?,
    val timestamp: Long,
)
