package ru.bartwell.kick.module.overlay.core.provider

import kotlinx.coroutines.CoroutineScope
import ru.bartwell.kick.module.overlay.OverlayAccessor

public class PerformanceOverlayProvider : OverlayProvider {
    override val categories: Set<String> = setOf(CATEGORY)

    @Suppress("EmptyFunctionBlock")
    override fun start(scope: CoroutineScope) {}

    @Suppress("EmptyFunctionBlock")
    override fun stop() {}

    public companion object {
        public const val CATEGORY: String = "Performance"
    }
}
