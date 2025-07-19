package ru.bartwell.kick.module.multiplatformsettings.feature.editor.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.russhwolf.settings.Settings
import ru.bartwell.kick.module.multiplatformsettings.feature.editor.extension.getAll

internal class DefaultSettingsEditorComponent(
    componentContext: ComponentContext,
    storageName: String,
    private val settings: Settings,
    private val onFinished: () -> Unit,
) : SettingsEditorComponent, ComponentContext by componentContext {

    private val _model = MutableValue(SettingsEditorState(storageName))
    override val model: Value<SettingsEditorState> = _model

    init {
        loadSettings()
    }

    override fun onBackPressed() {
        onFinished()
    }

    override fun onSavePressed() {
        for (key in model.value.keysToDelete) {
            settings.remove(key)
        }
        for (entry in model.value.entries) {
            settings.putString(entry.key, entry.value)
        }
        loadSettings()
    }

    override fun onValueChange(key: String, newValue: String) {
        val entries = _model.value.entries.toMutableMap()
        entries[key] = newValue
        _model.value = model.value.copy(entries = entries)
    }

    override fun onDeleteClick(key: String) {
        val entries = model.value.entries.toMutableMap()
        entries.remove(key)
        val keysToDelete = model.value.keysToDelete.toMutableList()
        keysToDelete.add(key)
        _model.value = model.value.copy(entries = entries, keysToDelete = keysToDelete)
    }

    private fun loadSettings() {
        _model.value = _model.value.copy(entries = settings.getAll())
    }
}
