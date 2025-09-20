package ru.bartwell.kick.module.overlay.core.persists

import com.russhwolf.settings.Settings
import ru.bartwell.kick.core.data.PlatformContext

internal object OverlaySettings {
    private lateinit var settings: Settings
    private const val KEY_ENABLED = "enabled"

    operator fun invoke(context: PlatformContext) {
        settings = PlatformSettingsFactory.create(context = context, name = "kick_overlay_prefs")
    }

    fun isEnabled(): Boolean = settings.getBoolean(KEY_ENABLED, false)

    fun setEnabled(value: Boolean) {
        settings.putBoolean(KEY_ENABLED, value)
    }
}

