package ru.bartwell.kick.module.overlay.core.provider

import kotlinx.coroutines.CoroutineScope
import ru.bartwell.kick.module.overlay.OverlayAccessor

public class PerformanceOverlayProvider : OverlayProvider {
    override val categories: Set<String> = setOf(CATEGORY)

    override fun start(scope: CoroutineScope, overlay: OverlayAccessor, category: String): OverlayProviderHandle {
        return OverlayProviderHandle {}
    }

    public companion object {
        private const val KEY_SEPARATOR: String = "::"
        public const val CATEGORY: String = "Performance"
        public const val CPU_USAGE_KEY: String = CATEGORY + KEY_SEPARATOR + "CPU"
        public const val MEMORY_USAGE_KEY: String = CATEGORY + KEY_SEPARATOR + "RAM"
        public const val CUSTOM_KEY_PREFIX: String = CATEGORY + KEY_SEPARATOR
    }
}
