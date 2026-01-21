package ru.bartwell.kick.module.controlpanel.core.actions

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import ru.bartwell.kick.module.controlpanel.data.ControlPanelEvent

internal object ControlPanelActions {
    private val _events = MutableSharedFlow<ControlPanelEvent>(
        replay = 64,
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val events: SharedFlow<ControlPanelEvent> = _events.asSharedFlow()

    var onButtonClick: ((id: String) -> Unit)? = null

    fun emitEvent(event: ControlPanelEvent) {
        _events.tryEmit(event)
    }

    fun emitButtonClick(id: String) {
        emitEvent(ControlPanelEvent.ButtonClicked(id))
        onButtonClick?.invoke(id)
    }
}
