package ru.bartwell.kick.module.logging.core.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import ru.bartwell.kick.core.util.DateUtils
import ru.bartwell.kick.module.logging.core.data.LogLevel
import ru.bartwell.kick.module.logging.core.persist.LogEntity

internal object Logger {

    private val loggerScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val logFlow = MutableSharedFlow<LogEntity>(
        replay = 0,
        extraBufferCapacity = 1_000,
        onBufferOverflow = BufferOverflow.SUSPEND
    )

    init {
        loggerScope.launch {
            logFlow.collect { entry ->
                @Suppress("TooGenericExceptionCaught")
                try {
                    DatabaseHolder.database
                        ?.getLogDao()
                        ?.insert(entry)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun log(level: LogLevel, message: String?) {
        if (message.isNullOrBlank()) return
        val entry = LogEntity(
            time = DateUtils.currentTimeMillis(),
            level = level,
            message = message
        )
        loggerScope.launch {
            logFlow.emit(entry)
        }
    }
}
