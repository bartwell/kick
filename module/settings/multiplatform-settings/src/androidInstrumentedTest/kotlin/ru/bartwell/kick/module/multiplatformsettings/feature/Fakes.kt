package ru.bartwell.kick.module.multiplatformsettings.feature

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import ru.bartwell.kick.module.multiplatformsettings.feature.editor.presentation.SettingsEditorComponent
import ru.bartwell.kick.module.multiplatformsettings.feature.editor.presentation.SettingsEditorState
import ru.bartwell.kick.module.multiplatformsettings.feature.list.presentation.SettingsListComponent
import ru.bartwell.kick.module.multiplatformsettings.feature.list.presentation.SettingsListState

internal class FakeSettingsListComponent(storages: List<String>) : SettingsListComponent {
    private val _model = MutableValue(SettingsListState(storages))
    override val model: Value<SettingsListState> get() = _model

    var backInvoked = false
        private set
    var clicked: String? = null
        private set

    override fun onBackPressed() { backInvoked = true }
    override fun onItemClick(storageName: String) { clicked = storageName }
}

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
