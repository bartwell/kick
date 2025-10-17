package ru.bartwell.kick.module.overlay.core.provider

import kotlinx.coroutines.CoroutineScope
import ru.bartwell.kick.module.overlay.OverlayAccessor

public interface OverlayProvider {
    public val categories: Set<String>

    public fun start(scope: CoroutineScope, overlay: OverlayAccessor, category: String): OverlayProviderHandle
}

public fun interface OverlayProviderHandle {
    public fun stop()
}
