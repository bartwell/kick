package ru.bartwell.kick.module.firebase.cloudmessaging.core.util

import ru.bartwell.kick.core.data.PlatformContext

private const val IOS_UNAVAILABLE_MESSAGE: String =
    "Firebase Cloud Messaging runtime is not included in this build"

internal actual object FirebaseWrapper {
    actual fun isFirebaseInitialized(
        context: PlatformContext,
    ): Boolean = false

    actual suspend fun getRegistrationToken(
        context: PlatformContext,
        forceRefresh: Boolean,
    ): Result<String> = Result.failure(IllegalStateException(IOS_UNAVAILABLE_MESSAGE))

    actual suspend fun getFirebaseInstallationId(
        context: PlatformContext,
    ): Result<String> =
        Result.failure(IllegalStateException(IOS_UNAVAILABLE_MESSAGE))
}
