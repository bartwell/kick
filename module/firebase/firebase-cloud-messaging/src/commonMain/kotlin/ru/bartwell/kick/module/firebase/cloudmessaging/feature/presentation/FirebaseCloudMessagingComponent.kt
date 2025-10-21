package ru.bartwell.kick.module.firebase.cloudmessaging.feature.presentation

import com.arkivanov.decompose.value.Value
import ru.bartwell.kick.core.component.Component
import ru.bartwell.kick.core.data.PlatformContext

internal interface FirebaseCloudMessagingComponent : Component {
    val state: Value<FirebaseCloudMessagingState>

    fun onBackPressed()

    fun refreshToken(forceRefresh: Boolean)
    fun refreshFirebaseId()
    fun copyToken(context: PlatformContext)
    fun copyFirebaseId(context: PlatformContext)

    fun refreshStatus()

    fun clearMessages()

    fun onLocalNotificationTitleChange(value: String)
    fun onLocalNotificationBodyChange(value: String)
    fun onLocalNotificationDataChange(value: String)
    fun onLocalNotificationChannelChange(value: String)
    fun sendLocalNotification()
    fun onLocalNotificationFeedbackConsumed()
}
