package ru.bartwell.kick.runtime.core.util

import ru.bartwell.kick.core.data.PlatformContext

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal expect object ShortcutManager {
    internal fun setup(context: PlatformContext)
}

internal val ShortcutManager.id: String
    get() = "kick_shortcut"

internal val ShortcutManager.title: String
    get() = "Kick"

internal val ShortcutManager.subtitle: String
    get() = "Inspect with Kick"
