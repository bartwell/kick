package ru.bartwell.kick.module.firebase.analytics.core.persist

import com.russhwolf.settings.Settings
import ru.bartwell.kick.core.data.PlatformContext

@Suppress(names = ["EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING"])
internal expect object PlatformSettingsFactory {
    fun create(context: PlatformContext, name: String): Settings
}
