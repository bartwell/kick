package ru.bartwell.kick.module.firebase.cloudmessaging.feature.main.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.module.firebase.cloudmessaging.core.util.FirebaseExternalUpdates
import ru.bartwell.kick.module.firebase.cloudmessaging.core.util.FirebaseWrapper
import ru.bartwell.kick.module.firebase.cloudmessaging.feature.main.extension.copyToClipboard

internal class DefaultFirebaseCloudMessagingComponent(
    componentContext: ComponentContext,
    private val onFinished: () -> Unit,
    private val onHistoryClickCallback: () -> Unit,
) : FirebaseCloudMessagingComponent, ComponentContext by componentContext {

    private val uiScope = coroutineScope()
    private val _state = MutableValue(
        FirebaseCloudMessagingState(
            availabilityMessage = NOT_INITIALISED_MESSAGE,
        )
    )
    override val state: Value<FirebaseCloudMessagingState> = _state

    override fun init(context: PlatformContext) {
        if (ensureFirebaseAvailability(context = context, requireFirebase = false)) {
            observeExternalValues()
            if (FirebaseWrapper.supportsAutoFetch) {
                refreshToken(context = context, forceRefresh = false)
                refreshFirebaseId(context = context)
            }
        } else {
            clearRemoteState()
        }
    }

    override fun onBackPressed() {
        onFinished()
    }

    override fun refreshToken(context: PlatformContext, forceRefresh: Boolean) {
        uiScope.launch {
            if (!ensureFirebaseAvailability(context = context, requireFirebase = true) { message ->
                    updateState { copy(tokenError = message, isTokenLoading = false) }
                }
            ) {
                return@launch
            }

            updateState { copy(isTokenLoading = true, tokenError = null) }
            val result = FirebaseWrapper.getRegistrationToken(context = context, forceRefresh = forceRefresh)
            updateState {
                result.fold(
                    onSuccess = { token ->
                        copy(token = token, tokenError = null, isTokenLoading = false)
                    },
                    onFailure = { error ->
                        copy(tokenError = error.message ?: error.toString(), isTokenLoading = false)
                    },
                )
            }
        }
    }

    override fun refreshFirebaseId(context: PlatformContext) {
        uiScope.launch {
            if (!ensureFirebaseAvailability(context = context, requireFirebase = true) { message ->
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
            val result = FirebaseWrapper.getFirebaseInstallationId(context)
            updateState {
                result.fold(
                    onSuccess = { id ->
                        copy(firebaseId = id, firebaseIdError = null, isFirebaseIdLoading = false)
                    },
                    onFailure = { error ->
                        copy(firebaseIdError = error.message ?: error.toString(), isFirebaseIdLoading = false)
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

    override fun onHistoryClick() {
        onHistoryClickCallback()
    }

    private fun updateState(block: FirebaseCloudMessagingState.() -> FirebaseCloudMessagingState) {
        _state.value = _state.value.block()
    }

    private fun ensureFirebaseAvailability(
        context: PlatformContext,
        requireFirebase: Boolean,
        onUnavailable: (String) -> Unit = {},
    ): Boolean {
        val available = FirebaseWrapper.isFirebaseInitialized(context)
        val message = if (available) null else NOT_INITIALISED_MESSAGE
        updateState {
            copy(
                isFirebaseAvailable = available,
                availabilityMessage = message,
                token = token.takeIf { available },
                firebaseId = firebaseId.takeIf { available },
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
                tokenError = null,
                firebaseIdError = null,
            )
        }
    }

    private fun observeExternalValues() {
        uiScope.launch {
            FirebaseExternalUpdates.fcmToken
                .filterNotNull()
                .collect { token ->
                    updateState { copy(token = token, tokenError = null, isTokenLoading = false) }
                }
        }
        uiScope.launch {
            FirebaseExternalUpdates.installationId
                .filterNotNull()
                .collect { id ->
                    updateState {
                        copy(firebaseId = id, firebaseIdError = null, isFirebaseIdLoading = false)
                    }
                }
        }
    }

    companion object {
        private const val NOT_INITIALISED_MESSAGE: String = "Firebase is not initialised in the host application"
    }
}
