package ru.bartwell.kick.module.multiplatformsettings.feature.editor.presentation

import com.arkivanov.decompose.value.Value
import ru.bartwell.kick.core.component.Component

internal interface SettingsEditorComponent : Component {
    val model: Value<SettingsEditorState>

    fun onBackPressed()
    fun onSavePressed()
    fun onValueChange(key: String, newValue: String)
    fun onDeleteClick(key: String)
}
