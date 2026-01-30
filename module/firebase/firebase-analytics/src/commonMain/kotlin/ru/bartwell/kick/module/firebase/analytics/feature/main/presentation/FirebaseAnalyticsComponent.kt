package ru.bartwell.kick.module.firebase.analytics.feature.main.presentation

import com.arkivanov.decompose.value.Value
import ru.bartwell.kick.core.component.Component

internal interface FirebaseAnalyticsComponent : Component {
    val model: Value<FirebaseAnalyticsState>

    fun onBackPressed()
    fun onClearEvents()
    fun onPropertiesClick()
}
