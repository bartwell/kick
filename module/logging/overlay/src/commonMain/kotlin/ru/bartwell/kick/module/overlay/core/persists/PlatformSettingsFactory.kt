package ru.bartwell.kick.module.overlay.core.persists

import com.russhwolf.settings.Settings
import ru.bartwell.kick.core.data.PlatformContext

internal expect object PlatformSettingsFactory {
    fun create(context: PlatformContext, name: String): Settings
}

