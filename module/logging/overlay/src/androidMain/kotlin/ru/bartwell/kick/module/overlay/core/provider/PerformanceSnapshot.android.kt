package ru.bartwell.kick.module.overlay.core.provider

import android.os.Build
import android.os.Process
import android.os.SystemClock
import ru.bartwell.kick.module.overlay.core.data.CpuTimes
import ru.bartwell.kick.module.overlay.core.data.MemoryInfo
import java.io.File

private const val PROC_STAT_PATH: String = "/proc/stat"
private const val PROC_MEMINFO_PATH: String = "/proc/meminfo"
private const val CPU_TOKEN_PREFIX: String = "cpu"
private const val CPU_MIN_TOKEN_COUNT: Int = 5
private const val CPU_IDLE_INDEX: Int = 3
private const val CPU_IOWAIT_INDEX: Int = 4
private const val CPU_PERCENT_FACTOR: Double = 100.0
private const val SPACE_DELIMITER: String = " "
private const val KEY_VALUE_DELIMITER: String = ":"
private const val KIB_IN_BYTES: Long = 1024L

private var previousCpuTimes: CpuTimes? = null
private var lastAppCpuTimeMs = 0L
private var lastWallTimeMs = 0L

internal actual fun readPerformanceSnapshot(): PerformanceSnapshot {
    val cpuUsage = readCpuUsage()
    val memoryInfo = readMemoryInfo()
    return PerformanceSnapshot(
        cpuUsagePercent = cpuUsage,
        usedMemoryBytes = memoryInfo?.used,
        totalMemoryBytes = memoryInfo?.total,
    )
}

private fun readCpuUsage(): Double? {
    var result: Double? = null

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val nowWall = SystemClock.uptimeMillis()
        val nowCpu = Process.getElapsedCpuTime()
        val prevWall = lastWallTimeMs
        val prevCpu = lastAppCpuTimeMs
        lastWallTimeMs = nowWall
        lastAppCpuTimeMs = nowCpu

        if (prevWall != 0L) {
            val deltaWall = (nowWall - prevWall).coerceAtLeast(1L)
            val deltaCpu = (nowCpu - prevCpu).coerceAtLeast(0L)
            val cores = Runtime.getRuntime().availableProcessors().coerceAtLeast(1)
            val percent = deltaCpu.toDouble() / (deltaWall.toDouble() * cores) * CPU_PERCENT_FACTOR
            result = percent.coerceIn(0.0, CPU_PERCENT_FACTOR)
        }
    } else {
        result = runCatching {
            val header = run {
                var line: String? = null
                File(PROC_STAT_PATH).useLines { seq ->
                    line = seq.firstOrNull { it.startsWith("$CPU_TOKEN_PREFIX ") }
                }
                line
            } ?: return@runCatching null

            val tokens = header
                .trim()
                .split(Regex("\\s+"))
                .takeIf { it.size >= CPU_MIN_TOKEN_COUNT && it.first() == CPU_TOKEN_PREFIX }
                ?.drop(1)
                ?.mapNotNull(String::toLongOrNull)
                ?: return@runCatching null

            val idle = (tokens.getOrNull(CPU_IDLE_INDEX) ?: 0L) +
                (tokens.getOrNull(CPU_IOWAIT_INDEX) ?: 0L)
            val total = tokens.sum()

            val prev = previousCpuTimes
            val current = CpuTimes(idle = idle, total = total)
            previousCpuTimes = current

            if (prev != null) {
                val deltaIdle = idle - prev.idle
                val deltaTotal = total - prev.total
                if (deltaTotal > 0) {
                    (1.0 - deltaIdle.toDouble() / deltaTotal.toDouble()) * CPU_PERCENT_FACTOR
                } else {
                    null
                }
            } else {
                (total - idle).toDouble() / total.toDouble() * CPU_PERCENT_FACTOR
            }
        }.getOrNull()
    }

    return result
}

private fun readMemoryInfo(): MemoryInfo? = runCatching {
    val memInfo = File(PROC_MEMINFO_PATH)
    if (!memInfo.exists()) {
        return@runCatching null
    }

    val values = mutableMapOf<String, Long>()
    memInfo.useLines { sequence ->
        sequence.forEach { line ->
            val parts = line.split(KEY_VALUE_DELIMITER, limit = 2)
            if (parts.size == 2) {
                val key = parts[0].trim()
                val value = parts[1].trim().split(SPACE_DELIMITER).firstOrNull()?.toLongOrNull()
                if (value != null) {
                    values[key] = value * KIB_IN_BYTES
                }
            }
        }
    }

    val total = values["MemTotal"]
    val available = values["MemAvailable"] ?: run {
        val free = values["MemFree"] ?: 0L
        val buffers = values["Buffers"] ?: 0L
        val cached = values["Cached"] ?: 0L
        free + buffers + cached
    }

    val used = if (total != null) total - available else null
    MemoryInfo(used = used, total = total)
}.getOrNull()
