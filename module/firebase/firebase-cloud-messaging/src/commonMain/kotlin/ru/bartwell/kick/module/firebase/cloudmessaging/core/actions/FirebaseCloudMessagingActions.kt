package ru.bartwell.kick.module.firebase.cloudmessaging.core.actions

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.module.firebase.cloudmessaging.core.data.FirebaseLocalNotificationRequest
import ru.bartwell.kick.module.firebase.cloudmessaging.core.data.FirebaseMessage
import ru.bartwell.kick.module.firebase.cloudmessaging.core.data.FirebaseNotificationStatus

internal object FirebaseCloudMessagingActions {
    private val _messages = MutableStateFlow<List<FirebaseMessage>>(emptyList())
    val messages: StateFlow<List<FirebaseMessage>> = _messages.asStateFlow()

    fun initialize(context: PlatformContext) {
        platformInitialize(context)
    }

    fun isFirebaseInitialized(): Boolean = platformIsFirebaseInitialized()

    suspend fun getRegistrationToken(forceRefresh: Boolean): Result<String> =
        platformGetRegistrationToken(forceRefresh)

    suspend fun getFirebaseInstallationId(): Result<String> =
        platformGetFirebaseInstallationId()

    suspend fun sendLocalNotification(request: FirebaseLocalNotificationRequest): Result<Unit> =
        platformSendLocalNotification(request)

    suspend fun getNotificationStatus(): Result<FirebaseNotificationStatus> =
        platformGetNotificationStatus()

    fun emitMessage(message: FirebaseMessage) {
        _messages.update { current ->
            (listOf(message) + current).take(MAX_MESSAGES)
        }
    }

    fun clearMessages() {
        _messages.value = emptyList()
    }
}

private const val MAX_MESSAGES: Int = 200

internal expect fun platformIsFirebaseInitialized(): Boolean

internal expect fun platformInitialize(context: PlatformContext)

internal expect suspend fun platformGetRegistrationToken(forceRefresh: Boolean): Result<String>

internal expect suspend fun platformGetFirebaseInstallationId(): Result<String>

internal expect suspend fun platformSendLocalNotification(
    request: FirebaseLocalNotificationRequest,
): Result<Unit>

internal expect suspend fun platformGetNotificationStatus(): Result<FirebaseNotificationStatus>
