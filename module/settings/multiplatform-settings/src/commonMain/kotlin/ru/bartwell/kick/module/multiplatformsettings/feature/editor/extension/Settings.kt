package ru.bartwell.kick.module.multiplatformsettings.feature.editor.extension

import com.russhwolf.settings.Settings

internal expect fun Settings.getAll(): Map<String, String>
