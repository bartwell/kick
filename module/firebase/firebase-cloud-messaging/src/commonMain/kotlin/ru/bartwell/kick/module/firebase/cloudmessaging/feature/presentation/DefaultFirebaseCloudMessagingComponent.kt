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

        if (ensureFirebaseAvailability(requireFirebase = false)) {
            refreshToken(forceRefresh = false)
            refreshFirebaseId()
            refreshStatus()
        } else {
            clearRemoteState()
        }
    }

    override fun onBackPressed() {
        onFinished()
    }

    override fun refreshToken(forceRefresh: Boolean) {
        uiScope.launch {
            if (!ensureFirebaseAvailability(requireFirebase = true) { message ->
                    updateState {
                        copy(
                            tokenError = message,
                            isTokenLoading = false,
                        )
                    }
                }
            ) {
                return@launch
            }

            updateState { copy(isTokenLoading = true, tokenError = null) }
            val result = FirebaseCloudMessagingActions.getRegistrationToken(forceRefresh)
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
            if (!ensureFirebaseAvailability(requireFirebase = true) { message ->
                    updateState {
                        copy(
                            firebaseIdError = message,
                            isFirebaseIdLoading = false,
                        )
                    }
                }
            ) {
                return@launch
            }

            updateState { copy(isFirebaseIdLoading = true, firebaseIdError = null) }
            val result = FirebaseCloudMessagingActions.getFirebaseInstallationId()
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
            if (!ensureFirebaseAvailability(requireFirebase = true) { message ->
                    updateState {
                        copy(
                            statusError = message,
                            isStatusLoading = false,
                        )
                    }
                }
            ) {
                return@launch
            }

            updateState { copy(isStatusLoading = true, statusError = null) }
            val result = FirebaseCloudMessagingActions.getNotificationStatus()
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
            ensureFirebaseAvailability(requireFirebase = false)

            val current = state.value.localNotification
            val dataResult = parseData(current.data)
            if (dataResult == null) {
                updateLocalNotification { copy(error = "Data must be a valid JSON object") }
                return@launch
            }

            updateLocalNotification { copy(isSending = true, error = null, successMessage = null) }
            val result = FirebaseCloudMessagingActions.sendLocalNotification(
                FirebaseLocalNotificationRequest(
                    title = current.title.takeIf { it.isNotBlank() },
                    body = current.body.takeIf { it.isNotBlank() },
                    data = dataResult,
                    channelId = current.channelId.takeIf { it.isNotBlank() },
                )
            )
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

    private fun ensureFirebaseAvailability(
        requireFirebase: Boolean,
        onUnavailable: (String) -> Unit = {},
    ): Boolean {
        val available = FirebaseCloudMessagingActions.isFirebaseInitialized()
        val message = if (available) null else NOT_INITIALISED_MESSAGE
        updateState {
            copy(
                isFirebaseAvailable = available,
                availabilityMessage = message,
                token = token.takeIf { available },
                firebaseId = firebaseId.takeIf { available },
                status = status.takeIf { available },
            )
        }
        if (!available && requireFirebase) {
            onUnavailable(NOT_INITIALISED_MESSAGE)
        }
        return available || !requireFirebase
    }

    private fun clearRemoteState() {
        updateState {
            copy(
                token = null,
                firebaseId = null,
                status = null,
                tokenError = null,
                firebaseIdError = null,
                statusError = null,
            )
        }
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

    private fun LocalNotificationState.clearFeedback(): LocalNotificationState = copy(
        error = null,
        successMessage = null,
    )

    companion object {
        private const val NOT_INITIALISED_MESSAGE: String = "Firebase is not initialised in the host application"
    }
}
