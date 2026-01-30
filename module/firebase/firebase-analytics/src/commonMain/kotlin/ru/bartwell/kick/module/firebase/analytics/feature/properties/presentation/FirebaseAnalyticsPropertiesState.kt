package ru.bartwell.kick.module.firebase.analytics.feature.properties.presentation

import ru.bartwell.kick.module.firebase.analytics.core.data.UserProperty

internal data class FirebaseAnalyticsPropertiesState(
    val properties: List<UserProperty> = emptyList(),
    val error: String? = null,
)
