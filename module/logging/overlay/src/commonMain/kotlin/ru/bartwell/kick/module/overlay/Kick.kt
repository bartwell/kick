package ru.bartwell.kick.module.overlay

import ru.bartwell.kick.Kick
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.module.overlay.core.overlay.KickOverlay
import ru.bartwell.kick.module.overlay.core.store.OverlayStore

public val Kick.Companion.overlay: OverlayAccessor
    get() = OverlayAccessor

@Suppress("TooManyFunctions")
public object OverlayAccessor {
    public fun clear() { OverlayStore.clear() }

    public fun set(key: String, value: Long) { OverlayStore.set(key, value.toString()) }
    public fun set(key: String, value: Int) { OverlayStore.set(key, value.toString()) }
    public fun set(key: String, value: Double) { OverlayStore.set(key, value.toString()) }
    public fun set(key: String, value: Float) { OverlayStore.set(key, value.toString()) }
    public fun set(key: String, value: String) { OverlayStore.set(key, value) }
    public fun set(key: String, value: Boolean) { OverlayStore.set(key, value.toString()) }

    public fun show(context: PlatformContext) { KickOverlay.show(context) }
    public fun hide() { KickOverlay.hide() }
}
