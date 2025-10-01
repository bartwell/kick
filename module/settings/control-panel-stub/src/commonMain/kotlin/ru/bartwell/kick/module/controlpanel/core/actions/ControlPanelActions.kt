package ru.bartwell.kick.module.controlpanel.core.actions

internal object ControlPanelActions {
    var onButtonClick: ((id: String) -> Unit)? = null

    fun emitButtonClick(id: String) {
        onButtonClick?.invoke(id)
    }
}
