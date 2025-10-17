@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package ru.bartwell.kick.module.overlay.core.provider

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.value
import platform.Foundation.NSProcessInfo
import platform.darwin.CPU_STATE_IDLE
import platform.darwin.CPU_STATE_NICE
import platform.darwin.CPU_STATE_SYSTEM
import platform.darwin.CPU_STATE_USER
import platform.darwin.HOST_CPU_LOAD_INFO
import platform.darwin.HOST_CPU_LOAD_INFO_COUNT
import platform.darwin.KERN_SUCCESS
import platform.darwin.MACH_TASK_BASIC_INFO
import platform.darwin.MACH_TASK_BASIC_INFO_COUNT
import platform.darwin.host_cpu_load_info
import platform.darwin.host_statistics
import platform.darwin.mach_host_self
import platform.darwin.mach_msg_type_number_tVar
import platform.darwin.mach_task_basic_info
import platform.darwin.mach_task_self_
import platform.darwin.task_info
import kotlin.native.concurrent.ThreadLocal

private data class CpuSample(val user: ULong, val nice: ULong, val system: ULong, val idle: ULong)

private const val PERCENT_FACTOR: Double = 100.0

@ThreadLocal
private object CpuState {
    var previous: CpuSample? = null
}

internal actual fun readPerformanceSnapshot(): PerformanceSnapshot {
    val cpu = readCpuUsage()
    val memory = readMemoryUsage()

    return PerformanceSnapshot(
        cpuUsagePercent = cpu,
        usedMemoryBytes = memory?.first,
        totalMemoryBytes = memory?.second,
    )
}

private fun readCpuUsage(): Double? = memScoped {
    val cpuInfo = alloc<host_cpu_load_info>()
    val count = alloc<mach_msg_type_number_tVar>().apply { value = HOST_CPU_LOAD_INFO_COUNT }

    val result = host_statistics(
        mach_host_self(),
        HOST_CPU_LOAD_INFO,
        cpuInfo.ptr.reinterpret(),
        count.ptr,
    )

    if (result != KERN_SUCCESS) {
        return null
    }

    val ticks = cpuInfo.cpu_ticks

    val sample = CpuSample(
        user = ticks[CPU_STATE_USER.toInt()].value.toULong(),
        nice = ticks[CPU_STATE_NICE.toInt()].value.toULong(),
        system = ticks[CPU_STATE_SYSTEM.toInt()].value.toULong(),
        idle = ticks[CPU_STATE_IDLE.toInt()].value.toULong(),
    )

    val previousSample = CpuState.previous
    CpuState.previous = sample

    if (previousSample == null) {
        return null
    }

    val userDelta = sample.activeTicks() - previousSample.activeTicks()
    val totalDelta = userDelta + (sample.idle - previousSample.idle)

    if (totalDelta.toLong() <= 0L) {
        return null
    }

    userDelta.toDouble() / totalDelta.toDouble() * PERCENT_FACTOR
}

private fun readMemoryUsage(): Pair<Long?, Long?>? = memScoped {
    val count = alloc<mach_msg_type_number_tVar>().apply { value = MACH_TASK_BASIC_INFO_COUNT }
    val info = alloc<mach_task_basic_info>()

    val result = task_info(
        target_task = mach_task_self_,
        flavor = MACH_TASK_BASIC_INFO,
        task_info_out = info.ptr.reinterpret(),
        task_info_outCnt = count.ptr,
    )

    val used = if (result == KERN_SUCCESS) info.resident_size.toLong() else null
    val total = NSProcessInfo.processInfo.physicalMemory.takeIf { it > 0uL }?.toLong()

    used to total
}

private fun CpuSample.activeTicks(): ULong = user + nice + system
