package ru.bartwell.kick.module.overlay.core.data

import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
internal object CpuState {
    var previous: CpuSample? = null
}