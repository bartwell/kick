package ru.bartwell.kick.sample.shared.setting

import com.russhwolf.settings.Settings

@Suppress("MagicNumber")
class DefaultSettings {

    val settings: Settings = Settings()

    init {
        settings.putInt("defaultInt", 123)
        settings.putLong("defaultLong", 321L)
        settings.putFloat("defaultFloat", 456.78f)
        settings.putDouble("defaultDouble", 78.456)
        settings.putString("defaultString", "Just a string")
        settings.putBoolean("defaultBoolean", true)
    }
}
