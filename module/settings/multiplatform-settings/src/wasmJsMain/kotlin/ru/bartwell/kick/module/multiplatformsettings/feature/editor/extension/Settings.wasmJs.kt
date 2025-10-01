package ru.bartwell.kick.module.multiplatformsettings.feature.editor.extension

import com.russhwolf.settings.Settings

internal actual fun Settings.getAll(): Map<String, String> {
    val entries = mutableMapOf<String, String>()
    for (key in keys) {
        entries[key] = getString(key, "")
    }
    return entries
}
