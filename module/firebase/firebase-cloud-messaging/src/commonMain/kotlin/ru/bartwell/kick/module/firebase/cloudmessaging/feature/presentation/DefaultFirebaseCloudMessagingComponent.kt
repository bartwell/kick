package ru.bartwell.kick.module.firebase.cloudmessaging.feature.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import ru.bartwell.kick.module.firebase.cloudmessaging.core.actions.FirebaseCloudMessagingActions
import ru.bartwell.kick.module.firebase.cloudmessaging.core.actions.FirebaseCloudMessagingDelegate
import ru.bartwell.kick.module.firebase.cloudmessaging.core.data.FirebaseLocalNotificationRequest
import ru.bartwell.kick.module.firebase.cloudmessaging.feature.extension.copyToClipboard

internal class DefaultFirebaseCloudMessagingComponent(
    componentContext: ComponentContext,
    private val onFinished: () -> Unit,
) : FirebaseCloudMessagingComponent, ComponentContext by componentContext {

    private val json = Json { ignoreUnknownKeys = true; prettyPrint = true }
    private val uiScope = coroutineScope()
    private val _state = MutableValue(
        FirebaseCloudMessagingState(
            availabilityMessage = NOT_INITIALISED_MESSAGE,
        )
    )
    override val state: Value<FirebaseCloudMessagingState> = _state

    init {
        FirebaseCloudMessagingActions.messages
            .onEach { messages ->
                updateState { copy(messages = messages) }
            }
            .launchIn(uiScope)

        FirebaseCloudMessagingActions.delegateFlow
            .onEach { delegate ->
                val isAvailable = delegate?.isFirebaseInitialized == true
                updateState {
                    copy(
                        isFirebaseAvailable = isAvailable,
                        availabilityMessage = if (isAvailable) null else NOT_INITIALISED_MESSAGE,
                    )
                }
                if (isAvailable) {
                    refreshToken(forceRefresh = false)
                    refreshFirebaseId()
                    refreshStatus()
                } else {
                    updateState {
                        copy(
                            token = null,
                            firebaseId = null,
                            status = null,
                        )
                    }
                }
            }
            .launchIn(uiScope)
    }

    override fun onBackPressed() {
        onFinished()
    }

    override fun refreshToken(forceRefresh: Boolean) {
        uiScope.launch {
            val delegate = FirebaseCloudMessagingActions.currentDelegate()
            if (!ensureDelegate(delegate)) {
                val message = if (delegate == null) NO_DELEGATE_MESSAGE else NOT_INITIALISED_MESSAGE
                updateState {
                    copy(
                        tokenError = message,
                        isTokenLoading = false,
                    )
                }
                return@launch
            }

            updateState { copy(isTokenLoading = true, tokenError = null) }
            val result = safeCall { delegate.getRegistrationToken(forceRefresh) }
            updateState { current ->
                result.fold(
                    onSuccess = { token ->
                        current.copy(token = token, tokenError = null, isTokenLoading = false)
                    },
                    onFailure = { error ->
                        current.copy(tokenError = error.message ?: error.toString(), isTokenLoading = false)
                    },
                )
            }
        }
    }

    override fun refreshFirebaseId() {
        uiScope.launch {
            val delegate = FirebaseCloudMessagingActions.currentDelegate()
            if (!ensureDelegate(delegate)) {
                val message = if (delegate == null) NO_DELEGATE_MESSAGE else NOT_INITIALISED_MESSAGE
                updateState {
                    copy(
                        firebaseIdError = message,
                        isFirebaseIdLoading = false,
                    )
                }
                return@launch
            }

            updateState { copy(isFirebaseIdLoading = true, firebaseIdError = null) }
            val result = safeCall { delegate.getFirebaseInstallationId() }
            updateState { current ->
                result.fold(
                    onSuccess = { id ->
                        current.copy(firebaseId = id, firebaseIdError = null, isFirebaseIdLoading = false)
                    },
                    onFailure = { error ->
                        current.copy(firebaseIdError = error.message ?: error.toString(), isFirebaseIdLoading = false)
                    },
                )
            }
        }
    }

    override fun copyToken(context: PlatformContext) {
        state.value.token?.let { token ->
            context.copyToClipboard("FCM Token", token)
        }
    }

    override fun copyFirebaseId(context: PlatformContext) {
        state.value.firebaseId?.let { id ->
            context.copyToClipboard("Firebase Installation Id", id)
        }
    }

    override fun refreshStatus() {
        uiScope.launch {
            val delegate = FirebaseCloudMessagingActions.currentDelegate()
            if (!ensureDelegate(delegate)) {
                val message = if (delegate == null) NO_DELEGATE_MESSAGE else NOT_INITIALISED_MESSAGE
                updateState {
                    copy(
                        statusError = message,
                        isStatusLoading = false,
                    )
                }
                return@launch
            }

            updateState { copy(isStatusLoading = true, statusError = null) }
            val result = safeCall { delegate.getNotificationStatus() }
            updateState { current ->
                result.fold(
                    onSuccess = { status ->
                        current.copy(status = status, statusError = null, isStatusLoading = false)
                    },
                    onFailure = { error ->
                        current.copy(statusError = error.message ?: error.toString(), isStatusLoading = false)
                    },
                )
            }
        }
    }

    override fun clearMessages() {
        FirebaseCloudMessagingActions.clearMessages()
    }

    override fun onLocalNotificationTitleChange(value: String) {
        updateLocalNotification { copy(title = value).clearFeedback() }
    }

    override fun onLocalNotificationBodyChange(value: String) {
        updateLocalNotification { copy(body = value).clearFeedback() }
    }

    override fun onLocalNotificationDataChange(value: String) {
        updateLocalNotification { copy(data = value).clearFeedback() }
    }

    override fun onLocalNotificationChannelChange(value: String) {
        updateLocalNotification { copy(channelId = value).clearFeedback() }
    }

    override fun sendLocalNotification() {
        uiScope.launch {
            val delegate = FirebaseCloudMessagingActions.currentDelegate()
            if (!ensureDelegate(delegate, requireFirebase = false)) {
                updateLocalNotification { copy(error = NO_DELEGATE_MESSAGE, isSending = false) }
                return@launch
            }

            val activeDelegate = delegate ?: return@launch

            val current = state.value.localNotification
            val dataResult = parseData(current.data)
            if (dataResult == null) {
                updateLocalNotification { copy(error = "Data must be a valid JSON object") }
                return@launch
            }

            updateLocalNotification { copy(isSending = true, error = null, successMessage = null) }
            val result = safeCall {
                activeDelegate.sendLocalNotification(
                    FirebaseLocalNotificationRequest(
                        title = current.title.takeIf { it.isNotBlank() },
                        body = current.body.takeIf { it.isNotBlank() },
                        data = dataResult,
                        channelId = current.channelId.takeIf { it.isNotBlank() },
                    )
                )
            }
            updateLocalNotification { local ->
                result.fold(
                    onSuccess = {
                        local.copy(
                            isSending = false,
                            successMessage = "Local notification sent",
                            error = null,
                        )
                    },
                    onFailure = { error ->
                        local.copy(
                            isSending = false,
                            error = error.message ?: error.toString(),
                            successMessage = null,
                        )
                    },
                )
            }
        }
    }

    override fun onLocalNotificationFeedbackConsumed() {
        updateLocalNotification { copy(successMessage = null, error = null) }
    }

    private fun updateState(block: FirebaseCloudMessagingState.() -> FirebaseCloudMessagingState) {
        _state.value = _state.value.block()
    }

    private fun updateLocalNotification(block: LocalNotificationState.() -> LocalNotificationState) {
        updateState { copy(localNotification = localNotification.block()) }
    }

    private fun ensureDelegate(
        delegate: FirebaseCloudMessagingDelegate?,
        requireFirebase: Boolean = true,
    ): Boolean {
        if (delegate == null) {
            updateState {
                copy(
                    isFirebaseAvailable = false,
                    availabilityMessage = NO_DELEGATE_MESSAGE,
                )
            }
            return false
        }

        val initialised = delegate.isFirebaseInitialized
        if (!initialised) {
            updateState {
                copy(
                    isFirebaseAvailable = false,
                    availabilityMessage = NOT_INITIALISED_MESSAGE,
                )
            }
            return !requireFirebase
        }

        updateState {
            copy(
                isFirebaseAvailable = true,
                availabilityMessage = null,
            )
        }
        return true
    }

    private fun parseData(raw: String): Map<String, String>? {
        val trimmed = raw.trim()
        if (trimmed.isBlank()) return emptyMap()
        return try {
            val element = json.parseToJsonElement(trimmed)
            if (element is JsonObject) {
                element.mapValues { entry ->
                    when (val value = entry.value) {
                        is JsonPrimitive -> value.content
                        else -> value.toString()
                    }
                }
            } else {
                null
            }
        } catch (error: Throwable) {
            null
        }
    }

    private suspend fun <T> safeCall(block: suspend () -> Result<T>): Result<T> = try {
        block()
    } catch (error: Throwable) {
        Result.failure(error)
    }

    private fun LocalNotificationState.clearFeedback(): LocalNotificationState = copy(
        error = null,
        successMessage = null,
    )

    companion object {
        private const val NOT_INITIALISED_MESSAGE: String = "Firebase is not initialised in the host application"
        private const val NO_DELEGATE_MESSAGE: String = "Firebase Cloud Messaging delegate is not registered"
    }
}
