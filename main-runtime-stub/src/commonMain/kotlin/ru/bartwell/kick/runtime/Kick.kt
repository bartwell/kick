package ru.bartwell.kick.runtime

import ru.bartwell.kick.Kick
import ru.bartwell.kick.core.data.PlatformContext

@Suppress("UnusedPrivateProperty", "EmptyFunctionBlock", "unused")
public fun Kick.Companion.init(context: PlatformContext, block: Kick.Configuration.() -> Unit) {
    init(EmptyKickImpl())
}
