package ru.bartwell.kick.module.firebase.cloudmessaging.core.util

import ru.bartwell.kick.core.data.PlatformContext

private const val TOKEN_UNAVAILABLE_MESSAGE: String =
    "FCM token is not available yet. Provide it from the host app."
private const val INSTALLATION_ID_UNAVAILABLE_MESSAGE: String =
    "Firebase installation id is not available yet. Provide it from the host app."

internal actual object FirebaseWrapper {
    actual val supportsAutoFetch: Boolean = false

    actual fun isFirebaseInitialized(
        context: PlatformContext,
    ): Boolean = true

    actual suspend fun getRegistrationToken(
        context: PlatformContext,
        forceRefresh: Boolean,
    ): Result<String> = runCatching {
        FirebaseExternalUpdates.fcmToken.value
            ?: throw IllegalStateException(TOKEN_UNAVAILABLE_MESSAGE)
    }

    actual suspend fun getFirebaseInstallationId(
        context: PlatformContext,
    ): Result<String> = runCatching {
        FirebaseExternalUpdates.installationId.value
            ?: throw IllegalStateException(INSTALLATION_ID_UNAVAILABLE_MESSAGE)
    }
}
