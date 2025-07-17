package ru.bartwell.kick.module.multiplatformsettings.feature.editor.presentation

internal data class SettingsEditorState(
    val storageName: String,
    val entries: Map<String, String> = emptyMap(),
    val keysToDelete: List<String> = emptyList(),
)
