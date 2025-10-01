package ru.bartwell.kick.module.controlpanel.core.persists

import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.core.data.get

@Suppress(names = ["EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING"])
internal actual object PlatformSettingsFactory {
    actual fun create(context: PlatformContext, name: String): Settings {
        return SharedPreferencesSettings.Factory(context.get())
            .create(name)
    }
}
