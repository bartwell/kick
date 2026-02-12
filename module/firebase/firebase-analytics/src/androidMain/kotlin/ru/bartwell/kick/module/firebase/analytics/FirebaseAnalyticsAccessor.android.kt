package ru.bartwell.kick.module.firebase.analytics

import android.os.Bundle
import ru.bartwell.kick.module.firebase.analytics.core.util.FirebaseAnalyticsLogger

public fun FirebaseAnalyticsAccessor.logEvent(name: String, params: Bundle?) {
    FirebaseAnalyticsLogger.logEvent(name = name, params = params.toParameterMap())
}

public fun FirebaseAnalyticsAccessor.setUserId(id: String?) {
    FirebaseAnalyticsLogger.setUserId(id)
}

public fun FirebaseAnalyticsAccessor.setUserProperty(name: String, value: String) {
    FirebaseAnalyticsLogger.setUserProperty(name, value)
}

private fun Bundle?.toParameterMap(): Map<String, String> {
    if (this == null) return emptyMap()
    return keySet().associateWith { key ->
        get(key)?.toString().orEmpty()
    }
}
