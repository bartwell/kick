package ru.bartwell.kick.module.controlpanel.feature.presentation

import ru.bartwell.kick.module.controlpanel.data.ControlPanelItem
import ru.bartwell.kick.module.controlpanel.data.InputType

internal data class ControlPanelState(
    val items: List<ControlPanelItem>,
    val values: Map<String, InputType>,
    val expanded: Map<String, Boolean> = emptyMap(),
)
