package ru.bartwell.kick.module.overlay.core.overlay

import ru.bartwell.kick.core.data.PlatformContext

public actual object KickOverlay {
    public actual fun init() {}
    public actual fun show(context: PlatformContext) {}
    public actual fun hide() {}
}

