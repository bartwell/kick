package ru.bartwell.kick.module.multiplatformsettings.core.component.child

import ru.bartwell.kick.core.component.Child
import ru.bartwell.kick.module.multiplatformsettings.feature.editor.presentation.SettingsEditorComponent

internal data class SettingsEditorChild(
    override val component: SettingsEditorComponent,
) : Child<SettingsEditorComponent>
