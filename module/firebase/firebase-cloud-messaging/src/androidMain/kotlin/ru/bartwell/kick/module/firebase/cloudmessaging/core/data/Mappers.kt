package ru.bartwell.kick.module.firebase.cloudmessaging.core.data

import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.RemoteMessage

private const val REMOTE_MESSAGE_PRIORITY_LOW: Int = -1

internal fun RemoteMessage.toFirebaseMessage(): FirebaseMessage {
    val notification = notification
    val channelId = notification?.channelId
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
    val payload = mutableMapOf<String, String>()
    val builder = PayloadBuilder(payload)
    builder.putData(data)
    builder.putIfNotNull("from", from)
    builder.putIfNotNull("to", to)
    builder.putIfNotNull("messageId", messageId)
    builder.putIfNotNull("sentTime", sentTime.takeIf { it != 0L })
    builder.putIfNotNull("collapseKey", collapseKey)
    builder.putIfNotNull("ttlSeconds", ttlSeconds)
    builder.putIfNotNull("messagePriority", messagePriority)
    builder.putIfNotNull("notificationPriority", notificationPriority)
    notification?.let { note ->
        addNotificationPayload(note, builder)
    }
    return payload
}

private fun addNotificationPayload(
    notification: RemoteMessage.Notification,
    builder: PayloadBuilder,
) {
    addNotificationText(notification, builder)
    addNotificationMeta(notification, builder)
    addNotificationFlags(notification, builder)
    addNotificationTiming(notification, builder)
}

private fun addNotificationText(
    notification: RemoteMessage.Notification,
    builder: PayloadBuilder,
) {
    builder.putIfNotNull("notification.title", notification.title)
    builder.putIfNotNull("notification.titleLocKey", notification.titleLocalizationKey)
    builder.putIfNotEmpty("notification.titleLocArgs", notification.titleLocalizationArgs)
    builder.putIfNotNull("notification.body", notification.body)
    builder.putIfNotNull("notification.bodyLocKey", notification.bodyLocalizationKey)
    builder.putIfNotEmpty("notification.bodyLocArgs", notification.bodyLocalizationArgs)
}

private fun addNotificationMeta(
    notification: RemoteMessage.Notification,
    builder: PayloadBuilder,
) {
    builder.putIfNotNull("notification.tag", notification.tag)
    builder.putIfNotNull("notification.icon", notification.icon)
    builder.putIfNotNull("notification.sound", notification.sound)
    builder.putIfNotNull("notification.imageUrl", notification.imageUrl?.toString())
    builder.putIfNotNull("notification.clickAction", notification.clickAction)
    builder.putIfNotNull("notification.color", notification.color)
    builder.putIfNotNull("notification.ticker", notification.ticker)
    builder.putIfNotNull("notification.link", notification.link?.toString())
    builder.putIfNotNull("notification.channelId", notification.channelId)
    builder.putIfNotNull(
        "notification.visibility",
        notification.visibility?.let(::mapNotificationVisibility),
    )
    builder.putIfNotNull("notification.badge", notification.notificationCount)
}

private fun addNotificationFlags(
    notification: RemoteMessage.Notification,
    builder: PayloadBuilder,
) {
    builder.putIfTrue("notification.sticky", notification.sticky)
    builder.putIfTrue("notification.localOnly", notification.localOnly)
    builder.putIfTrue("notification.defaultSound", notification.defaultSound)
    builder.putIfTrue("notification.defaultVibrate", notification.defaultVibrateSettings)
    builder.putIfTrue("notification.defaultLights", notification.defaultLightSettings)
}

private fun addNotificationTiming(
    notification: RemoteMessage.Notification,
    builder: PayloadBuilder,
) {
    builder.putIfNotNull("notification.eventTime", notification.eventTime)
    builder.putIfNotEmpty("notification.lightSettings", notification.lightSettings)
    builder.putIfNotEmpty("notification.vibrateTimings", notification.vibrateTimings)
}

private class PayloadBuilder(
    private val payload: MutableMap<String, String>,
) {
    fun putData(data: Map<String, String>) {
        data.forEach { (key, value) -> payload["data.$key"] = value }
    }

    fun putIfNotNull(key: String, value: Any?) {
        if (value != null) {
            payload[key] = value.toString()
        }
    }

    fun putIfTrue(key: String, value: Boolean?) {
        if (value == true) {
            payload[key] = true.toString()
        }
    }

    fun putIfNotEmpty(key: String, values: Array<out String>?) {
        if (!values.isNullOrEmpty()) {
            payload[key] = values.joinToString(prefix = "[", postfix = "]")
        }
    }

    fun putIfNotEmpty(key: String, values: IntArray?) {
        if (values != null && values.isNotEmpty()) {
            payload[key] = values.joinToString(prefix = "[", postfix = "]")
        }
    }

    fun putIfNotEmpty(key: String, values: LongArray?) {
        if (values != null && values.isNotEmpty()) {
            payload[key] = values.joinToString(prefix = "[", postfix = "]")
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
