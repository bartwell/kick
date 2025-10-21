package ru.bartwell.kick.module.overlay.core.data

internal data class CpuSample(val user: ULong, val nice: ULong, val system: ULong, val idle: ULong)
