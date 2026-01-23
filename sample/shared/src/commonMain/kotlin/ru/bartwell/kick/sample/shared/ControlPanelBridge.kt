package ru.bartwell.kick.sample.shared

import kotlinx.coroutines.flow.Flow
import ru.bartwell.kick.Kick
import ru.bartwell.kick.module.controlpanel.controlPanel
import ru.bartwell.kick.module.controlpanel.data.ControlPanelEvent

public fun controlPanelEvents(): Flow<ControlPanelEvent> = Kick.controlPanel.event

public fun controlPanelCloseButtonId(): String = CONTROL_PANEL_CLOSE_BUTTON_ID
