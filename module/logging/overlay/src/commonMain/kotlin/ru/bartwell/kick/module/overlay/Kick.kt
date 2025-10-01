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

    // Category-aware overloads
    public fun set(key: String, value: Long, category: String) { OverlayStore.set(key, value.toString(), category) }
    public fun set(key: String, value: Int, category: String) { OverlayStore.set(key, value.toString(), category) }
    public fun set(key: String, value: Double, category: String) { OverlayStore.set(key, value.toString(), category) }
    public fun set(key: String, value: Float, category: String) { OverlayStore.set(key, value.toString(), category) }
    public fun set(key: String, value: String, category: String) { OverlayStore.set(key, value, category) }
    public fun set(key: String, value: Boolean, category: String) { OverlayStore.set(key, value.toString(), category) }

    public fun show(context: PlatformContext) { KickOverlay.show(context) }
    public fun hide() { KickOverlay.hide() }
}
