package ru.bartwell.kick.module.overlay.core.overlay

import ru.bartwell.kick.core.data.PlatformContext

public expect object KickOverlay {
    public fun init(context: PlatformContext)
    public fun show(context: PlatformContext)
    public fun hide()
}
