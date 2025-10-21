package ru.bartwell.kick.module.firebase.cloudmessaging.core.actions

import ru.bartwell.kick.module.firebase.cloudmessaging.core.data.FirebaseLocalNotificationRequest
import ru.bartwell.kick.module.firebase.cloudmessaging.core.data.FirebaseNotificationStatus

public interface FirebaseCloudMessagingDelegate {
    public val isFirebaseInitialized: Boolean
    public suspend fun getRegistrationToken(forceRefresh: Boolean): Result<String>
    public suspend fun getFirebaseInstallationId(): Result<String>
    public suspend fun sendLocalNotification(request: FirebaseLocalNotificationRequest): Result<Unit>
    public suspend fun getNotificationStatus(): Result<FirebaseNotificationStatus>
}
