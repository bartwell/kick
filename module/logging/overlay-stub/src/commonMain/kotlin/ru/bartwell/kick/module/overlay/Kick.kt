package ru.bartwell.kick.module.overlay

import ru.bartwell.kick.Kick

public val Kick.Companion.overlay: OverlayAccessor
    get() = OverlayAccessor

@Suppress("TooManyFunctions", "UnusedParameter", "EmptyFunctionBlock")
public object OverlayAccessor {
    public fun clear() {}
    public fun set(key: String, value: Long) {}
    public fun set(key: String, value: Int) {}
    public fun set(key: String, value: Double) {}
    public fun set(key: String, value: Float) {}
    public fun set(key: String, value: String) {}
    public fun set(key: String, value: Boolean) {}
    public fun show() {}
    public fun hide() {}
}
