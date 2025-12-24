package ru.bartwell.kick.module.firebase.cloudmessaging.core.actions

import platform.Foundation.NSDictionary
import platform.UserNotifications.UNNotification
import ru.bartwell.kick.module.firebase.cloudmessaging.core.data.FirebaseMessage
import ru.bartwell.kick.module.firebase.cloudmessaging.FirebaseCloudMessagingAccessor

public fun FirebaseCloudMessagingAccessor.handleApnsPayload(userInfo: NSDictionary) {
    log(userInfo.toFirebaseMessage())
}

public fun FirebaseCloudMessagingAccessor.handleApnsPayload(userInfo: Map<Any?, *>) {
    log(userInfo.toFirebaseMessage())
}

public fun FirebaseCloudMessagingAccessor.handleApnsNotification(notification: UNNotification) {
    val payload = notification.request.content.userInfo
    log(payload.toFirebaseMessage())
}

private fun NSDictionary.toFirebaseMessage(): FirebaseMessage = entries().associate { entry ->
    val key = entry.key?.toString() ?: ""
    key to entry.value
}.toFirebaseMessage()

private fun Map<Any?, *>.toFirebaseMessage(): FirebaseMessage {
    val rawMap: Map<String, Any?> = entries
        .mapNotNull { (key, value) -> (key?.toString())?.let { it to value } }
        .toMap()

    val aps = rawMap["aps"] as? Map<*, *>
    val alert = when (val alertValue = aps?.get("alert")) {
        is String -> mapOf("body" to alertValue)
        is Map<*, *> -> alertValue.entries.associate { (k, v) -> (k?.toString() ?: "") to v }
        else -> emptyMap()
    }

    val data = rawMap.filterKeys { it != "aps" && !it.startsWith("gcm.") && it != "google.c.a.e" }
        .mapValues { it.value?.toString() ?: "" }

    val messageId = rawMap["gcm.message_id"] ?: rawMap["message_id"] ?: rawMap["google.message_id"]
    val collapseKey = rawMap["collapse_key"] ?: aps?.get("thread-id")
    val ttl = rawMap["gcm.ttl"] ?: rawMap["ttl"]
    val from = rawMap["from"] ?: rawMap["google.c.a.c_id"]

    return FirebaseMessage(
        title = alert["title"]?.toString() ?: alert["loc-key"]?.toString(),
        body = alert["body"]?.toString(),
        data = data,
        from = from?.toString(),
        to = rawMap["to"]?.toString(),
        messageId = messageId?.toString(),
        collapseKey = collapseKey?.toString(),
        category = (aps?.get("category") ?: rawMap["google.c.a.c_l"])?.toString(),
        threadId = aps?.get("thread-id")?.toString(),
        badge = aps?.get("badge")?.toString(),
        sound = aps?.get("sound")?.toString(),
        imageUrl = extractImage(alert)?.toString(),
        ttlSeconds = ttl?.toString()?.toLongOrNull(),
        raw = rawMap.mapValues { it.value?.toString() ?: "" },
    )
}

private fun extractImage(alert: Map<String, Any?>): Any? {
    return alert["image"] ?: alert["image-url"] ?: alert["imageURL"]
}

private fun NSDictionary.entries(): List<MapEntry> {
    val keys = allKeys
    val result = mutableListOf<MapEntry>()
    repeat(keys.count.toInt()) { index ->
        val key = keys.objectAtIndex(index.toULong())
        val value = objectForKey(key)
        result += MapEntry(key, value)
    }
    return result
}

private data class MapEntry(val key: Any?, val value: Any?)
