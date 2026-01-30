package ru.bartwell.kick.module.firebase.analytics

import platform.Foundation.NSDictionary
import ru.bartwell.kick.module.firebase.analytics.core.util.FirebaseAnalyticsLogger

public fun FirebaseAnalyticsAccessor.logEvent(name: String, params: NSDictionary?) {
    FirebaseAnalyticsLogger.logEvent(name = name, params = params.toParameterMap())
}

public fun FirebaseAnalyticsAccessor.logEvent(name: String, params: Map<Any?, *>?) {
    FirebaseAnalyticsLogger.logEvent(name = name, params = params.toParameterMap())
}

public fun FirebaseAnalyticsAccessor.setUserId(id: String?) {
    FirebaseAnalyticsLogger.setUserId(id)
}

public fun FirebaseAnalyticsAccessor.setUserProperty(name: String, value: String) {
    FirebaseAnalyticsLogger.setUserProperty(name, value)
}

private fun NSDictionary?.toParameterMap(): Map<String, String> {
    if (this == null) return emptyMap()
    val map = mutableMapOf<String, String>()
    val enumerator = keyEnumerator() ?: return emptyMap()
    while (true) {
        val key = enumerator.nextObject() ?: break
        val keyString = key.toString()
        val value = objectForKey(key)?.toString() ?: continue
        map[keyString] = value
    }
    return map
}

private fun Map<Any?, *>?.toParameterMap(): Map<String, String> {
    if (this == null) return emptyMap()
    return mapNotNull { (key, value) ->
        val keyString = key?.toString() ?: return@mapNotNull null
        val valueString = value?.toString()
        valueString?.let { keyString to it }
    }.toMap()
}
