package ru.bartwell.kick.sample.shared.setting

import com.russhwolf.settings.Settings
import ru.bartwell.kick.core.data.PlatformContext

@Suppress("MagicNumber")
class CustomSettings(context: PlatformContext) {

    val settings: Settings = PlatformSettingsFactory.create(context = context, name = "custom_prefs")

    init {
        settings.putInt("customInt", 999)
        settings.putLong("customLong", 654_321L)
        settings.putFloat("customFloat", 1.23f)
        settings.putDouble("customDouble", 9.876)
        settings.putString("customString", "Sample string")
        settings.putBoolean("customBoolean", false)
    }
}
