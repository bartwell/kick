package ru.bartwell.kick.module.firebase.cloudmessaging.feature.history.presentation

import com.arkivanov.decompose.value.Value
import ru.bartwell.kick.core.component.Component
import ru.bartwell.kick.module.firebase.cloudmessaging.core.data.FirebaseMessage

internal interface FirebaseCloudMessagingHistoryComponent : Component {
    val state: Value<FirebaseCloudMessagingHistoryState>

    fun onBackPressed()
    fun onClearMessages()
    fun onMessageClick(message: FirebaseMessage)
}
