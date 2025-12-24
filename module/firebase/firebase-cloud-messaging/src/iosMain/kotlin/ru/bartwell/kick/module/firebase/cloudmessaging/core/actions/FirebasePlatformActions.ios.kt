package ru.bartwell.kick.module.firebase.cloudmessaging.core.actions

import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.module.firebase.cloudmessaging.core.data.FirebaseLocalNotificationRequest
import ru.bartwell.kick.module.firebase.cloudmessaging.core.data.FirebaseNotificationStatus

private const val IOS_UNAVAILABLE_MESSAGE: String =
    "Firebase Cloud Messaging runtime is not included in this build"

internal actual fun platformInitialize(context: PlatformContext) {
    // Nothing to initialise on iOS for the sample implementation.
}

internal actual fun platformIsFirebaseInitialized(): Boolean = false

internal actual suspend fun platformGetRegistrationToken(
    forceRefresh: Boolean,
): Result<String> = Result.failure(IllegalStateException(IOS_UNAVAILABLE_MESSAGE))

internal actual suspend fun platformGetFirebaseInstallationId(): Result<String> =
    Result.failure(IllegalStateException(IOS_UNAVAILABLE_MESSAGE))

internal actual suspend fun platformSendLocalNotification(
    request: FirebaseLocalNotificationRequest,
): Result<Unit> = Result.failure(IllegalStateException(IOS_UNAVAILABLE_MESSAGE))

internal actual suspend fun platformGetNotificationStatus(): Result<FirebaseNotificationStatus> =
    Result.failure(IllegalStateException(IOS_UNAVAILABLE_MESSAGE))
