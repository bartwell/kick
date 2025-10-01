package ru.bartwell.kick.module.controlpanel.core.persists

import com.russhwolf.settings.Settings
import ru.bartwell.kick.core.data.PlatformContext

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING", "unused")
internal expect object PlatformSettingsFactory {
    fun create(context: PlatformContext, name: String): Settings
}
