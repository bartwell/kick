package ru.bartwell.kick.module.firebase.cloudmessaging.feature.presentation

import ru.bartwell.kick.module.firebase.cloudmessaging.core.data.FirebaseMessage
import ru.bartwell.kick.module.firebase.cloudmessaging.core.data.FirebaseNotificationStatus

internal data class FirebaseCloudMessagingState(
    val isFirebaseAvailable: Boolean = false,
    val token: String? = null,
    val tokenError: String? = null,
    val isTokenLoading: Boolean = false,
    val firebaseId: String? = null,
    val firebaseIdError: String? = null,
    val isFirebaseIdLoading: Boolean = false,
    val status: FirebaseNotificationStatus? = null,
    val statusError: String? = null,
    val isStatusLoading: Boolean = false,
    val messages: List<FirebaseMessage> = emptyList(),
    val availabilityMessage: String? = null,
    val localNotification: LocalNotificationState = LocalNotificationState(),
)

internal data class LocalNotificationState(
    val title: String = "",
    val body: String = "",
    val data: String = "{}",
    val channelId: String = "",
    val isSending: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
)
