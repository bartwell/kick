package ru.bartwell.kick.module.firebase.cloudmessaging

import platform.Foundation.NSDictionary
import platform.UserNotifications.UNNotification
import ru.bartwell.kick.module.firebase.cloudmessaging.core.data.toFirebaseMessage
import ru.bartwell.kick.module.firebase.cloudmessaging.core.util.FirebaseMessageLogger

public fun FirebaseCloudMessagingAccessor.handleApnsPayload(userInfo: NSDictionary) {
    FirebaseMessageLogger.log(userInfo.toFirebaseMessage())
}

public fun FirebaseCloudMessagingAccessor.handleApnsPayload(userInfo: Map<*, *>) {
    FirebaseMessageLogger.log(userInfo.toFirebaseMessage())
}

public fun FirebaseCloudMessagingAccessor.handleApnsNotification(notification: UNNotification) {
    val payload = notification.request.content.userInfo
    FirebaseMessageLogger.log(payload.toFirebaseMessage())
}
