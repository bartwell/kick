package ru.bartwell.kick.module.firebase.analytics.core.data

import kotlinx.serialization.Serializable

@Serializable
internal data class AnalyticsEvent(
    val id: Long = 0,
    val timestamp: Long,
    val name: String,
    val params: Map<String, String> = emptyMap(),
)
