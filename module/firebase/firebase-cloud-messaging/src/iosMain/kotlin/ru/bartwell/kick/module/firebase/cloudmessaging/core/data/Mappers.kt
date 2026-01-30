package ru.bartwell.kick.module.firebase.cloudmessaging.core.data

import platform.Foundation.NSDictionary
import platform.Foundation.NSMutableDictionary
import platform.Foundation.NSString
import platform.Foundation.allKeys

internal fun NSDictionary.toFirebaseMessage(): FirebaseMessage = entries().associate { entry ->
    val key = entry.key?.toString() ?: ""
    key to entry.value
}.toFirebaseMessage()

internal fun Map<*, *>.toFirebaseMessage(): FirebaseMessage {
    val rawMap = buildRawMap(this)
    val aps = extractAps(rawMap)
    val alert = extractAlert(aps)
    val data = extractData(rawMap)
    return buildFirebaseMessage(
        rawMap = rawMap,
        aps = aps,
        alert = alert,
        data = data,
    )
}

internal fun Map<Any?, *>.toNSDictionary(): NSDictionary {
    val dictionary = NSMutableDictionary()
    forEach { (key, value) ->
        if (key != null && value != null) {
            val keyString = key.toString() as NSString
            dictionary.setObject(value.toObjcValue(), forKey = keyString)
        }
    }
    return dictionary
}

private fun extractImage(alert: Map<String, Any?>): Any? {
    return alert["image"] ?: alert["image-url"] ?: alert["imageURL"]
}

private fun buildRawMap(source: Map<*, *>): Map<String, Any?> = source.entries
    .mapNotNull { (key, value) -> key?.toString()?.let { it to value } }
    .toMap()

private fun extractAps(rawMap: Map<String, Any?>): Map<*, *>? = rawMap["aps"] as? Map<*, *>

private fun extractAlert(aps: Map<*, *>?): Map<String, Any?> {
    return when (val alertValue = aps?.get("alert")) {
        is String -> mapOf("body" to alertValue)
        is Map<*, *> -> alertValue.entries.associate { (key, value) -> (key?.toString() ?: "") to value }
        else -> emptyMap()
    }
}

private fun extractData(rawMap: Map<String, Any?>): Map<String, String> {
    return rawMap
        .filterKeys { it != "aps" && !it.startsWith("gcm.") && it != "google.c.a.e" }
        .mapValues { it.value?.toString() ?: "" }
}

private fun buildFirebaseMessage(
    rawMap: Map<String, Any?>,
    aps: Map<*, *>?,
    alert: Map<String, Any?>,
    data: Map<String, String>,
): FirebaseMessage {
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

private fun NSDictionary.entries(): List<MapEntry> {
    val keys = allKeys
    val result = mutableListOf<MapEntry>()
    repeat(keys.count()) { index ->
        val key = keys[index]
        val value = objectForKey(key)
        result += MapEntry(key, value)
    }
    return result
}

private fun Any?.toObjcValue(): Any? = if (this is Map<*, *>) {
    (this as Map<Any?, *>).toNSDictionary()
} else {
    this
}

private data class MapEntry(val key: Any?, val value: Any?)
