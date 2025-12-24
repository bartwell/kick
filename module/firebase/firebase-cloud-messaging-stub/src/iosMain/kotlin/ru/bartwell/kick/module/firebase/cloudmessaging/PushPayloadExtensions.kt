package ru.bartwell.kick.module.firebase.cloudmessaging

import platform.Foundation.NSDictionary
import platform.UserNotifications.UNNotification

public fun FirebaseCloudMessagingAccessor.handleApnsPayload(userInfo: NSDictionary) {
    // No-op in the stub artifact.
}

public fun FirebaseCloudMessagingAccessor.handleApnsPayload(userInfo: Map<Any?, *>) {
    // No-op in the stub artifact.
}

public fun FirebaseCloudMessagingAccessor.handleApnsNotification(notification: UNNotification) {
    // No-op in the stub artifact.
}
