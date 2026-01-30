package ru.bartwell.kick.module.firebase.cloudmessaging.feature.detail.presentation

import com.arkivanov.decompose.value.Value
import ru.bartwell.kick.core.component.Component

internal interface FirebaseCloudMessagingDetailsComponent : Component {
    val state: Value<FirebaseCloudMessagingDetailsState>

    fun onBackPressed()
}
