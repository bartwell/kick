package ru.bartwell.kick.module.controlpanel.data

public sealed class ControlPanelEvent {
    public data object ModuleExited : ControlPanelEvent()
    public data object SaveClicked : ControlPanelEvent()
    public data class ValueChanged(
        val name: String,
        val value: InputType,
    ) : ControlPanelEvent()

    public data class ButtonClicked(
        val id: String,
    ) : ControlPanelEvent()
}
