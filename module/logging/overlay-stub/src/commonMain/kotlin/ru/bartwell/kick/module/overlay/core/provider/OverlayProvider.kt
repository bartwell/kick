package ru.bartwell.kick.module.overlay.core.provider

import kotlinx.coroutines.CoroutineScope

public interface OverlayProvider {
    public val categories: Set<String>

    public fun start(scope: CoroutineScope)

    public fun stop()
}
