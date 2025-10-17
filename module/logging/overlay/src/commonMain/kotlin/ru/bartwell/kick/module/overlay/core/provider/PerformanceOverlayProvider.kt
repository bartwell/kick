package ru.bartwell.kick.module.overlay.core.provider

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import ru.bartwell.kick.module.overlay.OverlayAccessor
import kotlin.math.roundToInt

public class PerformanceOverlayProvider(
    private val updateIntervalMillis: Long = 1_000,
) : OverlayProvider {

    override val categories: Set<String> = setOf(CATEGORY)

    override fun start(scope: CoroutineScope, overlay: OverlayAccessor, category: String): OverlayProviderHandle {
        require(category == CATEGORY) { "PerformanceOverlayProvider started for unexpected category: $category" }

        overlay.set(CPU_USAGE_KEY, NOT_AVAILABLE_VALUE)
        overlay.set(MEMORY_USAGE_KEY, NOT_AVAILABLE_VALUE)

        val job = scope.launch {
            while (isActive) {
                val snapshot = readPerformanceSnapshot()
                overlay.set(CPU_USAGE_KEY, snapshot.cpuUsagePercent?.let(::formatPercent) ?: NOT_AVAILABLE_VALUE)
                overlay.set(MEMORY_USAGE_KEY, formatMemory(snapshot))
                delay(updateIntervalMillis)
            }
        }

        return OverlayProviderHandle {
            job.cancel()
        }
    }

    private fun formatPercent(value: Double): String {
        val normalized = if (!value.isFinite()) {
            return NOT_AVAILABLE_VALUE
        } else {
            ((value * PERCENT_PRECISION_MULTIPLIER).roundToInt() / PERCENT_PRECISION_DIVISOR)
                .coerceIn(MIN_PERCENT, MAX_PERCENT)
        }

        val displayValue = normalized.takeIf { it >= 0 } ?: 0.0
        val formatted = displayValue.toString()
        return if (formatted.contains(DECIMAL_SEPARATOR)) "$formatted %" else "$formatted$DEFAULT_DECIMAL_SUFFIX %"
    }

    private fun formatMemory(snapshot: PerformanceSnapshot): String {
        val used = snapshot.usedMemoryBytes
        val total = snapshot.totalMemoryBytes

        return when {
            used != null && total != null -> "${formatBytes(used)} / ${formatBytes(total)}"
            used != null -> formatBytes(used)
            total != null -> formatBytes(total)
            else -> NOT_AVAILABLE_VALUE
        }
    }

    private fun formatBytes(value: Long): String {
        if (value <= 0L) return "0 B"

        var unitIndex = 0
        var remaining = value.toDouble()
        while (remaining >= UNIT_STEP && unitIndex < BYTE_UNITS.lastIndex) {
            remaining /= UNIT_STEP
            unitIndex++
        }

        val rounded = (remaining * PERCENT_PRECISION_MULTIPLIER).roundToInt() / PERCENT_PRECISION_DIVISOR
        val normalized = if (rounded % 1.0 == 0.0) {
            rounded.roundToInt().toString()
        } else {
            rounded.toString()
        }

        return "$normalized ${BYTE_UNITS[unitIndex]}"
    }

    private fun Double.isFinite(): Boolean = !isNaN() && !isInfinite()

    public companion object {
        private const val KEY_SEPARATOR: String = "::"
        private const val PERCENT_PRECISION_MULTIPLIER: Int = 10
        private const val PERCENT_PRECISION_DIVISOR: Double = 10.0
        private const val MIN_PERCENT: Double = 0.0
        private const val MAX_PERCENT: Double = 100.0
        private const val DECIMAL_SEPARATOR: String = "."
        private const val DEFAULT_DECIMAL_SUFFIX: String = ".0"
        private const val UNIT_STEP: Double = 1024.0
        public const val CATEGORY: String = "Performance"
        public const val CPU_USAGE_KEY: String = CATEGORY + KEY_SEPARATOR + "CPU"
        public const val MEMORY_USAGE_KEY: String = CATEGORY + KEY_SEPARATOR + "RAM"
        public const val CUSTOM_KEY_PREFIX: String = CATEGORY + KEY_SEPARATOR
        private const val NOT_AVAILABLE_VALUE: String = "—"
        private val BYTE_UNITS = arrayOf("B", "KB", "MB", "GB", "TB", "PB")
    }
}
