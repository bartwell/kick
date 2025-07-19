package ru.bartwell.kick.module.multiplatformsettings.feature.editor.extension

import com.russhwolf.settings.Settings

internal actual fun Settings.getAll(): Map<String, String> {
    val entries = mutableMapOf<String, String>()
    for (key in keys) {
        entries[key] = getAsString(key).orEmpty()
    }
    return entries
}

private fun Settings.getAsString(key: String): String? {
    return listOf<(String) -> Any?>(
        { getLong(it, 0L) },
        { getBoolean(it, false) },
        { getInt(it, 0) },
        { getFloat(it, 0f) },
        { getDouble(it, .0) },
        { getString(it, "") }
    )
        .asSequence()
        .map { reader -> runCatching { reader(key)?.toString() } }
        .firstOrNull { it.isSuccess }
        ?.getOrNull()
}
