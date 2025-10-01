package ru.bartwell.kick.module.controlpanel.core.persists

import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings
import platform.Foundation.NSUserDefaults
import ru.bartwell.kick.core.data.PlatformContext

@Suppress(names = ["EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING"])
internal actual object PlatformSettingsFactory {
    actual fun create(context: PlatformContext, name: String): Settings {
        val userDefaults = NSUserDefaults(suiteName = name)
        return NSUserDefaultsSettings(userDefaults)
    }
}
