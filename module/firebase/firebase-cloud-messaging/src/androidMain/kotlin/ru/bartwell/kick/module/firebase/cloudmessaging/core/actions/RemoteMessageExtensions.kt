package ru.bartwell.kick.module.firebase.cloudmessaging.core.actions

import com.google.firebase.messaging.RemoteMessage
import ru.bartwell.kick.Kick
import ru.bartwell.kick.module.firebase.cloudmessaging.firebaseCloudMessaging
import ru.bartwell.kick.module.firebase.cloudmessaging.core.data.FirebaseMessage

public fun Kick.Companion.logFirebaseMessage(message: RemoteMessage) {
    firebaseCloudMessaging.log(message.toFirebaseMessage())
}

private fun RemoteMessage.toFirebaseMessage(): FirebaseMessage {
    val notification = notification
    val channelId = notification?.androidChannelId ?: notification?.channelId
    val ttl = if (ttl == 0) null else ttl.toLong()
    val priority = when (priority) {
        RemoteMessage.PRIORITY_HIGH -> "high"
        RemoteMessage.PRIORITY_NORMAL -> "normal"
        else -> priority.toString()
    }
    return FirebaseMessage(
        title = notification?.title,
        body = notification?.body,
        data = data,
        from = from,
        to = to,
        messageId = messageId,
        sentTimeMillis = sentTime.takeIf { it != 0L },
        collapseKey = collapseKey,
        channelId = channelId,
        tag = notification?.tag,
        imageUrl = notification?.imageUrl?.toString(),
        priority = priority,
        ttlSeconds = ttl,
        raw = data,
    )
}
