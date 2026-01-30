package ru.bartwell.kick.module.controlpanel.core.component.child

import ru.bartwell.kick.core.component.Child
import ru.bartwell.kick.module.controlpanel.feature.main.presentation.ControlPanelComponent

internal data class ControlPanelChild(
    override val component: ControlPanelComponent,
) : Child<ControlPanelComponent>
