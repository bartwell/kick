package ru.bartwell.kick.module.multiplatformsettings.core.component.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.bartwell.kick.core.component.Config

@Serializable
@SerialName("SettingsEditorConfig")
internal data class SettingsEditorConfig(val storageName: String) : Config
