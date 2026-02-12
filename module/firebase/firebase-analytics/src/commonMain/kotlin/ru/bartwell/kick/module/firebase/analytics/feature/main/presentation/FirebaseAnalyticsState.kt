package ru.bartwell.kick.module.firebase.analytics.feature.main.presentation

import ru.bartwell.kick.module.firebase.analytics.core.data.AnalyticsEvent
import ru.bartwell.kick.module.firebase.analytics.core.data.UserProperty

internal data class FirebaseAnalyticsState(
    val events: List<AnalyticsEvent> = emptyList(),
    val properties: List<UserProperty> = emptyList(),
    val userId: String? = null,
    val error: String? = null,
)
