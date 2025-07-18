package ru.bartwell.kick.module.logging.core.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import ru.bartwell.kick.core.util.DateUtils
import ru.bartwell.kick.module.logging.core.data.LogLevel
import ru.bartwell.kick.module.logging.core.persist.LogEntity

internal object Logger {

    private val loggerScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val logFlow = MutableSharedFlow<LogEntity>(
        extraBufferCapacity = 1_000,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    init {
        loggerScope.launch {
            logFlow.collect { entry ->
                DatabaseHolder.database
                    ?.getLogDao()
                    ?.insert(entry)
            }
        }
    }

    fun log(level: LogLevel, message: String?) {
        message ?: return
        val entry = LogEntity(
            time = DateUtils.currentTimeMillis(),
            level = level,
            message = message
        )
        logFlow.tryEmit(entry)
    }
}
