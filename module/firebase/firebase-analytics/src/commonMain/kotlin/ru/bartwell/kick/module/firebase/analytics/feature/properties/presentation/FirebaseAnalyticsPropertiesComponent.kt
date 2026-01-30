package ru.bartwell.kick.module.firebase.analytics.feature.properties.presentation

import com.arkivanov.decompose.value.Value
import ru.bartwell.kick.core.component.Component

internal interface FirebaseAnalyticsPropertiesComponent : Component {
    val model: Value<FirebaseAnalyticsPropertiesState>

    fun onBackPressed()
}
