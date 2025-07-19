package ru.bartwell.kick.sample.shared.setting

import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.core.data.get

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object PlatformSettingsFactory {
    actual fun create(context: PlatformContext, name: String): Settings =
        SharedPreferencesSettings.Factory(context.get())
            .create(name)
}
