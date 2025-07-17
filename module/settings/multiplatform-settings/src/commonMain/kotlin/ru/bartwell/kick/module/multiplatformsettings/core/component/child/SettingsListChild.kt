package ru.bartwell.kick.module.multiplatformsettings.core.component.child

import ru.bartwell.kick.core.component.Child
import ru.bartwell.kick.module.multiplatformsettings.feature.list.presentation.SettingsListComponent

internal data class SettingsListChild(
    override val component: SettingsListComponent,
) : Child<SettingsListComponent>
