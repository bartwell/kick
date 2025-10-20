package ru.bartwell.kick.module.overlay.core.provider

import kotlinx.coroutines.CoroutineScope
import ru.bartwell.kick.module.overlay.OverlayAccessor

public interface OverlayProvider {
    public val categories: Set<String>

    public fun start(scope: CoroutineScope)

    public fun stop()
}
