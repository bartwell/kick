package ru.bartwell.kick.module.firebase.analytics.core.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import ru.bartwell.kick.core.util.DateUtils
import ru.bartwell.kick.module.firebase.analytics.core.data.AnalyticsEvent
import ru.bartwell.kick.module.firebase.analytics.core.data.UserProperty
import ru.bartwell.kick.module.firebase.analytics.core.persist.FirebaseFloatingWindowSettings
import ru.bartwell.kick.module.firebase.analytics.core.persist.toEntity

private const val MAX_BUFFER = 1_000

internal object FirebaseAnalyticsLogger {

    private val loggerScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val eventFlow = MutableSharedFlow<AnalyticsEvent>(
        replay = 0,
        extraBufferCapacity = MAX_BUFFER,
        onBufferOverflow = BufferOverflow.SUSPEND,
    )

    private val propertyFlow = MutableSharedFlow<UserProperty>(
        replay = 0,
        extraBufferCapacity = MAX_BUFFER,
        onBufferOverflow = BufferOverflow.SUSPEND,
    )

    init {
        loggerScope.launch {
            eventFlow.collect { entry ->
                runCatching {
                    DatabaseHolder.database
                        ?.getEventDao()
                        ?.insert(entry.toEntity())
                }.onFailure { it.printStackTrace() }
            }
        }
        loggerScope.launch {
            propertyFlow.collect { entry ->
                runCatching {
                    DatabaseHolder.database
                        ?.getPropertyDao()
                        ?.upsert(entry.toEntity())
                }.onFailure { it.printStackTrace() }
            }
        }
    }

    fun logEvent(name: String, params: Map<String, String>) {
        if (name.isBlank()) return
        val event = AnalyticsEvent(
            timestamp = DateUtils.currentTimeMillis(),
            name = name,
            params = params,
        )
        FirebaseFloatingWindowState.append(buildEventLine(name, params))
        loggerScope.launch { eventFlow.emit(event) }
    }

    fun setUserId(id: String?) {
        FirebaseFloatingWindowState.append(buildUserIdLine(id))
        loggerScope.launch { FirebaseFloatingWindowSettings.setUserId(id) }
    }

    fun setUserProperty(name: String, value: String) {
        if (name.isBlank()) return
        val property = UserProperty(
            name = name,
            value = value,
            timestamp = DateUtils.currentTimeMillis(),
        )
        FirebaseFloatingWindowState.append(buildPropertyLine(name, value))
        loggerScope.launch { propertyFlow.emit(property) }
    }
}

private fun buildEventLine(name: String, params: Map<String, String>): String {
    val suffix = if (params.isEmpty()) {
        ""
    } else {
        params.entries.joinToString(
            prefix = " {",
            postfix = "}"
        ) { "${it.key}=${it.value}" }
    }
    return "Event $name$suffix"
}

private fun buildUserIdLine(id: String?): String =
    if (id.isNullOrBlank()) "User ID cleared" else "User ID $id"

private fun buildPropertyLine(name: String, value: String): String =
    "Property $name=$value"
