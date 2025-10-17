package ru.bartwell.kick.module.overlay.core.provider

import java.io.File
import java.io.RandomAccessFile

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

private data class CpuTimes(val idle: Long, val total: Long)

private var previousCpuTimes: CpuTimes? = null

internal actual fun readPerformanceSnapshot(): PerformanceSnapshot {
    val cpuUsage = readCpuUsage()
    val memoryInfo = readMemoryInfo()
    return PerformanceSnapshot(
        cpuUsagePercent = cpuUsage,
        usedMemoryBytes = memoryInfo?.used,
        totalMemoryBytes = memoryInfo?.total,
    )
}

private fun readCpuUsage(): Double? =
    runCatching {
        RandomAccessFile(PROC_STAT_PATH, "r").use { reader ->
            val values = reader.readLine()
                ?.split(SPACE_DELIMITER)
                ?.filter { it.isNotBlank() }
                ?.takeIf { it.size >= CPU_MIN_TOKEN_COUNT && it.first() == CPU_TOKEN_PREFIX }
                ?.drop(1)
                ?.mapNotNull(String::toLongOrNull)

            if (values.isNullOrEmpty()) {
                null
            } else {
                val idle = (values.getOrNull(CPU_IDLE_INDEX) ?: 0L) +
                    (values.getOrNull(CPU_IOWAIT_INDEX) ?: 0L)
                val total = values.sum()

                val current = CpuTimes(idle = idle, total = total)
                val previous = previousCpuTimes
                previousCpuTimes = current

                previous?.let {
                    val deltaIdle = idle - it.idle
                    val deltaTotal = total - it.total
                    if (deltaTotal > 0) {
                        (1.0 - deltaIdle.toDouble() / deltaTotal.toDouble()) * CPU_PERCENT_FACTOR
                    } else {
                        null
                    }
                }
            }
        }
    }.getOrNull()

private data class MemoryInfo(val used: Long?, val total: Long?)

private fun readMemoryInfo(): MemoryInfo? =
    runCatching {
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

        val used = if (total != null && available != null) total - available else null
        MemoryInfo(used = used, total = total)
    }.getOrNull()
