package ru.bartwell.kick.module.overlay.core.provider

import kotlinx.coroutines.CoroutineScope

public interface OverlayProvider {

    /**
     * Categories
     * List all of categories used by provider. It is important for correct start/stop provider.
     */
    public val categories: Set<String>

    public val isAvailable: Boolean

    public fun start(scope: CoroutineScope)

    public fun stop()
}
