package ru.bartwell.kick.module.firebase.cloudmessaging.core.util

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal actual object FirebaseExternalUpdates {
    private val fcmTokenState = MutableStateFlow<String?>(null)
    private val installationIdState = MutableStateFlow<String?>(null)

    actual val fcmToken: StateFlow<String?> = fcmTokenState
    actual val installationId: StateFlow<String?> = installationIdState

    actual fun updateFcmToken(token: String?) {
        fcmTokenState.value = token
    }

    actual fun updateInstallationId(id: String?) {
        installationIdState.value = id
    }
}
