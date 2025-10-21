package ru.bartwell.kick.module.firebase.cloudmessaging.core.actions

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import ru.bartwell.kick.module.firebase.cloudmessaging.core.data.FirebaseLocalNotificationRequest
import ru.bartwell.kick.module.firebase.cloudmessaging.core.data.FirebaseMessage
import ru.bartwell.kick.module.firebase.cloudmessaging.core.data.FirebaseNotificationStatus

/**
 * API that the host application needs to implement so the module can talk back to Firebase SDK.
 */
public interface FirebaseCloudMessagingDelegate {
    /**
     * Indicates that Firebase SDK was fully initialised and can be queried safely.
     */
    public val isFirebaseInitialized: Boolean

    /**
     * Returns current FCM registration token.
     */
    public suspend fun getRegistrationToken(forceRefresh: Boolean): Result<String>

    /**
     * Returns Firebase installation identifier used by the current device.
     */
    public suspend fun getFirebaseInstallationId(): Result<String>

    /**
     * Triggers a local notification emulating an incoming FCM push.
     */
    public suspend fun sendLocalNotification(request: FirebaseLocalNotificationRequest): Result<Unit>

    /**
     * Collects platform specific notification status information.
     */
    public suspend fun getNotificationStatus(): Result<FirebaseNotificationStatus>
}

internal object FirebaseCloudMessagingActions {
    private val _messages = MutableStateFlow<List<FirebaseMessage>>(emptyList())
    val messages: StateFlow<List<FirebaseMessage>> = _messages.asStateFlow()

    private val _delegate = MutableStateFlow<FirebaseCloudMessagingDelegate?>(null)
    val delegateFlow: StateFlow<FirebaseCloudMessagingDelegate?> = _delegate.asStateFlow()

    fun currentDelegate(): FirebaseCloudMessagingDelegate? = _delegate.value

    fun registerDelegate(delegate: FirebaseCloudMessagingDelegate) {
        _delegate.value = delegate
    }

    fun unregisterDelegate(delegate: FirebaseCloudMessagingDelegate?) {
        if (_delegate.value == delegate || delegate == null) {
            _delegate.value = null
        }
    }

    fun emitMessage(message: FirebaseMessage) {
        _messages.update { current -> listOf(message) + current }
    }

    fun clearMessages() {
        _messages.value = emptyList()
    }
}
