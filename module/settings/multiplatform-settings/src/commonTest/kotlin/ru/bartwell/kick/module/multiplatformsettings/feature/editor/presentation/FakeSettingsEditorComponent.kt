package ru.bartwell.kick.module.multiplatformsettings.feature.editor.presentation

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value

internal class FakeSettingsEditorComponent(
    name: String = "default",
    entries: Map<String, String> = mapOf("a" to "1", "b" to "2"),
) : SettingsEditorComponent {
    private val _model = MutableValue(SettingsEditorState(storageName = name, entries = entries))
    override val model: Value<SettingsEditorState> get() = _model

    var backInvoked = false
        private set
    var saveInvoked = false
        private set
    val deleted = mutableListOf<String>()

    override fun onBackPressed() { backInvoked = true }
    override fun onSavePressed() { saveInvoked = true }
    override fun onValueChange(key: String, newValue: String) {
        _model.value = model.value.copy(entries = model.value.entries.toMutableMap().apply { put(key, newValue) })
    }
    override fun onDeleteClick(key: String) { deleted.add(key) }
}
