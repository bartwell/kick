package ru.bartwell.kick.module.firebase.cloudmessaging.core.util

import kotlinx.coroutines.flow.StateFlow

internal expect object FirebaseExternalUpdates {
    val fcmToken: StateFlow<String?>
    val installationId: StateFlow<String?>

    fun updateFcmToken(token: String?)
    fun updateInstallationId(id: String?)
}
