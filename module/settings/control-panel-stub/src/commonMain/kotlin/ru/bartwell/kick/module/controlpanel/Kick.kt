package ru.bartwell.kick.module.controlpanel

import ru.bartwell.kick.Kick
import ru.bartwell.kick.module.controlpanel.data.ControlPanelAccessor

public val Kick.Companion.controlPanel: ControlPanelAccessor
    get() = ControlPanelAccessor()
