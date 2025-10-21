package ru.bartwell.kick.module.overlay.core.provider

import com.sun.management.OperatingSystemMXBean
import java.lang.management.ManagementFactory

private const val PERCENT_FACTOR: Double = 100.0

private val osBean: OperatingSystemMXBean? =
    ManagementFactory.getOperatingSystemMXBean() as? OperatingSystemMXBean

internal actual fun readPerformanceSnapshot(): PerformanceSnapshot {
    val cpu = osBean?.systemCpuLoad?.takeIf { it >= 0 }?.let { it * PERCENT_FACTOR }
    val totalMemory = osBean?.totalPhysicalMemorySize?.takeIf { it > 0 }
    val freeMemory = osBean?.freePhysicalMemorySize?.takeIf { it >= 0 }
    val usedMemory = if (totalMemory != null && freeMemory != null) totalMemory - freeMemory else null

    return PerformanceSnapshot(
        cpuUsagePercent = cpu,
        usedMemoryBytes = usedMemory,
        totalMemoryBytes = totalMemory,
    )
}
