@file:OptIn(ExperimentalForeignApi::class)

package ru.bartwell.kick.module.overlay.core.provider

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.UIntVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.plus
import kotlinx.cinterop.pointed
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
import platform.darwin.host_cpu_load_info_data_t
import platform.darwin.host_statistics
import platform.darwin.mach_host_self
import platform.darwin.mach_msg_type_number_tVar
import platform.darwin.mach_task_basic_info
import platform.darwin.mach_task_self_
import platform.darwin.task_info
import ru.bartwell.kick.module.overlay.core.data.CpuSample
import ru.bartwell.kick.module.overlay.core.data.CpuState

private const val PERCENT_FACTOR: Double = 100.0

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
    val cpuInfo = alloc<host_cpu_load_info_data_t>()
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

    val ticksPtr: CPointer<UIntVar> = cpuInfo.cpu_ticks

    fun tick(cpuStateIndex: Int) = (ticksPtr + cpuStateIndex)!!.pointed.value.toULong()

    val sample = CpuSample(
        user   = tick(CPU_STATE_USER),
        nice   = tick(CPU_STATE_NICE),
        system = tick(CPU_STATE_SYSTEM),
        idle   = tick(CPU_STATE_IDLE),
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
    val count = alloc<mach_msg_type_number_tVar>().apply {
        value = MACH_TASK_BASIC_INFO_COUNT.convert()
    }
    val info = alloc<mach_task_basic_info>()

    val result = task_info(
        target_task = mach_task_self_,
        flavor = MACH_TASK_BASIC_INFO.toUInt(),
        task_info_out = info.ptr.reinterpret(),
        task_info_outCnt = count.ptr,
    )

    val used = if (result == KERN_SUCCESS) info.resident_size.toLong() else null
    val total = NSProcessInfo.processInfo.physicalMemory.takeIf { it > 0uL }?.toLong()

    used to total
}

private fun CpuSample.activeTicks(): ULong = user + nice + system
