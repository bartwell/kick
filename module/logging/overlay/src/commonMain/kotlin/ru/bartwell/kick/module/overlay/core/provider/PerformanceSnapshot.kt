package ru.bartwell.kick.module.overlay.core.provider

internal data class PerformanceSnapshot(
    val cpuUsagePercent: Double?,
    val usedMemoryBytes: Long?,
    val totalMemoryBytes: Long?,
)

internal expect fun readPerformanceSnapshot(): PerformanceSnapshot
