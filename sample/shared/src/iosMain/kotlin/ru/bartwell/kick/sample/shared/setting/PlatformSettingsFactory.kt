package ru.bartwell.kick.sample.shared.setting

import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings
import platform.Foundation.NSUserDefaults
import ru.bartwell.kick.core.data.PlatformContext

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object PlatformSettingsFactory {
    actual fun create(context: PlatformContext, name: String): Settings {
        val userDefaults = NSUserDefaults(suiteName = name)
        return NSUserDefaultsSettings(userDefaults)
    }
}
