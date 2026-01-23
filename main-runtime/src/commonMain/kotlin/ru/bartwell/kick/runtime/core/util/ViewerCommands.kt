package ru.bartwell.kick.runtime.core.util

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

internal object ViewerCommands {
    private val _closeRequests = MutableSharedFlow<Unit>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    val closeRequests: SharedFlow<Unit> = _closeRequests

    fun requestClose() {
        _closeRequests.tryEmit(Unit)
    }
}
