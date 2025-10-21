package ru.bartwell.kick.module.overlay.core.provider

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import ru.bartwell.kick.Kick
import ru.bartwell.kick.core.data.Platform
import ru.bartwell.kick.core.util.PlatformUtils
import ru.bartwell.kick.module.overlay.overlay
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

private const val PERCENT_PRECISION_MULTIPLIER: Int = 10
private const val PERCENT_PRECISION_DIVISOR: Double = 10.0
private const val MIN_PERCENT: Double = 0.0
private const val MAX_PERCENT: Double = 100.0
private const val DECIMAL_SEPARATOR: String = "."
private const val DEFAULT_DECIMAL_SUFFIX: String = ".0"
private const val UNIT_STEP: Double = 1024.0
public const val CATEGORY: String = "Performance"
private const val CPU_USAGE_KEY: String = "CPU"
private const val MEMORY_USAGE_KEY: String = "RAM"
private const val NOT_AVAILABLE_VALUE: String = "—"
private val BYTE_UNITS = arrayOf("B", "KB", "MB", "GB", "TB", "PB")

public class PerformanceOverlayProvider(
    private val updateIntervalMillis: Duration = 1.seconds,
) : OverlayProvider {

    override val categories: Set<String> = setOf(CATEGORY)
    override val isAvailable: Boolean
        get() = PlatformUtils.getPlatform() != Platform.WEB
    private var job: Job? = null

    override fun start(scope: CoroutineScope) {
        Kick.overlay.set(CPU_USAGE_KEY, NOT_AVAILABLE_VALUE, CATEGORY)
        Kick.overlay.set(MEMORY_USAGE_KEY, NOT_AVAILABLE_VALUE, CATEGORY)

        job = scope.launch {
            while (isActive) {
                val snapshot = readPerformanceSnapshot()
                Kick.overlay.set(
                    key = CPU_USAGE_KEY,
                    value = snapshot.cpuUsagePercent?.let(::formatPercent) ?: NOT_AVAILABLE_VALUE,
                    category = CATEGORY,
                )
                Kick.overlay.set(
                    key = MEMORY_USAGE_KEY,
                    value = formatMemory(snapshot),
                    category = CATEGORY,
                )
                delay(updateIntervalMillis)
            }
        }
    }

    override fun stop() {
        job?.cancel()
        job = null
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
}
