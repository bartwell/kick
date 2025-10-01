package ru.bartwell.kick.module.controlpanel.core.persists

import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import ru.bartwell.kick.core.data.PlatformContext
import java.util.prefs.Preferences

@Suppress(names = ["EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING"])
internal actual object PlatformSettingsFactory {
    actual fun create(context: PlatformContext, name: String): Settings {
        val node = Preferences.userRoot().node(name)
        return PreferencesSettings(node)
    }
}
