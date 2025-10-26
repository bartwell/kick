package ru.bartwell.kick.module.firebase.cloudmessaging.core.actions

import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.RemoteMessage
import ru.bartwell.kick.module.firebase.cloudmessaging.FirebaseCloudMessagingAccessor
import ru.bartwell.kick.module.firebase.cloudmessaging.core.data.FirebaseMessage

public fun FirebaseCloudMessagingAccessor.handleFcm(message: RemoteMessage) {
    log(message.toFirebaseMessage())
}

private fun RemoteMessage.toFirebaseMessage(): FirebaseMessage {
    val notification = notification
    val channelId = notification?.androidChannelId ?: notification?.channelId
    val ttl = if (ttl == 0) null else ttl.toLong()
    val messagePriority = mapMessagePriority(priority)
    val notificationPriority = notification?.notificationPriority?.let(::mapNotificationPriority)
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
        category = notification?.clickAction,
        badge = notification?.notificationCount?.takeIf { it > 0 }?.toString(),
        tag = notification?.tag,
        sound = notification?.sound,
        imageUrl = notification?.imageUrl?.toString(),
        priority = notificationPriority ?: messagePriority,
        ttlSeconds = ttl,
        raw = buildRawPayload(
            notification = notification,
            messagePriority = messagePriority,
            notificationPriority = notificationPriority,
            ttlSeconds = ttl,
        ),
    )
}

private fun RemoteMessage.buildRawPayload(
    notification: RemoteMessage.Notification?,
    messagePriority: String?,
    notificationPriority: String?,
    ttlSeconds: Long?,
): Map<String, String> {
    val note = notification
    return buildMap {
        data.forEach { (key, value) -> put("data.$key", value) }
        from?.let { put("from", it) }
        to?.let { put("to", it) }
        messageId?.let { put("messageId", it) }
        sentTime.takeIf { it != 0L }?.let { put("sentTime", it.toString()) }
        collapseKey?.let { put("collapseKey", it) }
        ttlSeconds?.let { put("ttlSeconds", it.toString()) }
        messagePriority?.let { put("messagePriority", it) }
        notificationPriority?.let { put("notificationPriority", it) }
        note?.title?.let { put("notification.title", it) }
        note?.titleLocalizationKey?.let { put("notification.titleLocKey", it) }
        note?.titleLocalizationArgs?.takeUnless { it.isEmpty() }?.let {
            put("notification.titleLocArgs", it.joinToString(prefix = "[", postfix = "]"))
        }
        note?.body?.let { put("notification.body", it) }
        note?.bodyLocalizationKey?.let { put("notification.bodyLocKey", it) }
        note?.bodyLocalizationArgs?.takeUnless { it.isEmpty() }?.let {
            put("notification.bodyLocArgs", it.joinToString(prefix = "[", postfix = "]"))
        }
        note?.tag?.let { put("notification.tag", it) }
        note?.icon?.let { put("notification.icon", it) }
        note?.sound?.let { put("notification.sound", it) }
        note?.imageUrl?.toString()?.let { put("notification.imageUrl", it) }
        note?.clickAction?.let { put("notification.clickAction", it) }
        note?.color?.let { put("notification.color", it) }
        note?.ticker?.let { put("notification.ticker", it) }
        note?.link?.toString()?.let { put("notification.link", it) }
        note?.channelId?.let { put("notification.channelId", it) }
        note?.androidChannelId?.let { put("notification.androidChannelId", it) }
        note?.visibility?.let { put("notification.visibility", mapNotificationVisibility(it)) }
        note?.notificationCount?.let { put("notification.badge", it.toString()) }
        if (note?.sticky == true) put("notification.sticky", true.toString())
        if (note?.localOnly == true) put("notification.localOnly", true.toString())
        if (note?.defaultSound == true) put("notification.defaultSound", true.toString())
        if (note?.defaultVibrateSettings == true) {
            put("notification.defaultVibrate", true.toString())
        }
        if (note?.defaultLightSettings == true) {
            put("notification.defaultLights", true.toString())
        }
        note?.eventTime?.let { put("notification.eventTime", it.toString()) }
        note?.lightSettings?.takeUnless { it.isEmpty() }?.let {
            put("notification.lightSettings", it.joinToString(prefix = "[", postfix = "]"))
        }
        note?.vibrateTimings?.takeUnless { it.isEmpty() }?.let {
            put("notification.vibrateTimings", it.joinToString(prefix = "[", postfix = "]"))
        }
    }
}

private fun mapMessagePriority(priority: Int): String? = when (priority) {
    RemoteMessage.PRIORITY_UNKNOWN -> "unknown"
    RemoteMessage.PRIORITY_HIGH -> "high"
    RemoteMessage.PRIORITY_NORMAL -> "normal"
    else -> if (priority == REMOTE_MESSAGE_PRIORITY_LOW) "low" else priority.toString()
}

private fun mapNotificationPriority(priority: Int): String = when (priority) {
    NotificationCompat.PRIORITY_MAX -> "max"
    NotificationCompat.PRIORITY_HIGH -> "high"
    NotificationCompat.PRIORITY_DEFAULT -> "default"
    NotificationCompat.PRIORITY_LOW -> "low"
    NotificationCompat.PRIORITY_MIN -> "min"
    else -> priority.toString()
}

private fun mapNotificationVisibility(visibility: Int): String = when (visibility) {
    NotificationCompat.VISIBILITY_PUBLIC -> "public"
    NotificationCompat.VISIBILITY_PRIVATE -> "private"
    NotificationCompat.VISIBILITY_SECRET -> "secret"
    else -> visibility.toString()
}

private const val REMOTE_MESSAGE_PRIORITY_LOW: Int = -1
