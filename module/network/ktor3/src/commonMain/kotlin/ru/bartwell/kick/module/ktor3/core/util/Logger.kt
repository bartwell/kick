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
        extraBufferCapacity = 1_000,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    init {
        scope.launch {
            flow.collect { entry ->
                DatabaseHolder.database
                    ?.getRequestDao()
                    ?.insert(entry)
            }
        }
    }

    fun log(entry: RequestEntity) {
        flow.tryEmit(entry)
    }
}
