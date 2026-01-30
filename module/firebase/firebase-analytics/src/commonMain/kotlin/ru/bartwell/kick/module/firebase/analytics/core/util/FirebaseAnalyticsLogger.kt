package ru.bartwell.kick.module.firebase.analytics.core.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import ru.bartwell.kick.core.util.DateUtils
import ru.bartwell.kick.module.firebase.analytics.core.data.AnalyticsEvent
import ru.bartwell.kick.module.firebase.analytics.core.data.UserId
import ru.bartwell.kick.module.firebase.analytics.core.data.UserProperty
import ru.bartwell.kick.module.firebase.analytics.core.persist.toEntity

private const val MAX_BUFFER = 1_000

internal object FirebaseAnalyticsLogger {

    private val loggerScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val eventFlow = MutableSharedFlow<AnalyticsEvent>(
        replay = 0,
        extraBufferCapacity = MAX_BUFFER,
        onBufferOverflow = BufferOverflow.SUSPEND,
    )

    private val userIdFlow = MutableSharedFlow<UserId>(
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
                try {
                    DatabaseHolder.database
                        ?.getEventDao()
                        ?.insert(entry.toEntity())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        loggerScope.launch {
            userIdFlow.collect { entry ->
                try {
                    DatabaseHolder.database
                        ?.getUserIdDao()
                        ?.insert(entry.toEntity())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        loggerScope.launch {
            propertyFlow.collect { entry ->
                try {
                    DatabaseHolder.database
                        ?.getPropertyDao()
                        ?.upsert(entry.toEntity())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
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
        loggerScope.launch { eventFlow.emit(event) }
    }

    fun setUserId(id: String?) {
        val userId = UserId(
            value = id,
            timestamp = DateUtils.currentTimeMillis(),
        )
        loggerScope.launch { userIdFlow.emit(userId) }
    }

    fun setUserProperty(name: String, value: String) {
        if (name.isBlank()) return
        val property = UserProperty(
            name = name,
            value = value,
            timestamp = DateUtils.currentTimeMillis(),
        )
        loggerScope.launch { propertyFlow.emit(property) }
    }
}
