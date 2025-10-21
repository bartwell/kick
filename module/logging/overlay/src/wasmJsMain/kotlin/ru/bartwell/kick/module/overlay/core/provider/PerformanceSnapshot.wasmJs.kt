package ru.bartwell.kick.module.overlay.core.provider

internal actual fun readPerformanceSnapshot(): PerformanceSnapshot =
    PerformanceSnapshot(cpuUsagePercent = null, usedMemoryBytes = null, totalMemoryBytes = null)
