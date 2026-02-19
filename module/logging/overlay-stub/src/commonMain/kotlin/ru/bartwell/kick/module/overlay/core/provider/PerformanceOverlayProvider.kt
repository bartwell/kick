package ru.bartwell.kick.module.overlay.core.provider

import kotlinx.coroutines.CoroutineScope
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Suppress("UnusedPrivateProperty")
public class PerformanceOverlayProvider(
    private val updateIntervalMillis: Duration,
) : OverlayProvider {
    override val categories: Set<String> = setOf(CATEGORY)
    override val isAvailable: Boolean = true

    public constructor() : this(1.seconds)

    @Suppress("EmptyFunctionBlock")
    override fun start(scope: CoroutineScope) {}

    @Suppress("EmptyFunctionBlock")
    override fun stop() {}

    public companion object {
        public const val CATEGORY: String = "Performance"
    }
}
