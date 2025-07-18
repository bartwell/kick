package ru.bartwell.kick.module.ktor3.core.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import ru.bartwell.kick.core.persist.RequestEntity

internal object Logger {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val flow = MutableSharedFlow<RequestEntity>(
        replay = 0,
        extraBufferCapacity = 1_000,
        onBufferOverflow = BufferOverflow.SUSPEND
    )

    init {
        scope.launch {
            flow.collect { entry ->
                @Suppress("TooGenericExceptionCaught")
                try {
                    DatabaseHolder.database
                        ?.getRequestDao()
                        ?.insert(entry)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun log(entry: RequestEntity) {
        scope.launch {
            flow.emit(entry)
        }
    }
}
