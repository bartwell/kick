package ru.bartwell.kick.module.configuration.core.persists

import com.russhwolf.settings.Settings
import ru.bartwell.kick.core.data.PlatformContext

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal actual object PlatformSettingsFactory {
    actual fun create(context: PlatformContext, name: String): Settings = Settings()
}
