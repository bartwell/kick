package ru.bartwell.kick.module.layout.core.trigger

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreMotion.CMMotionManager
import platform.Foundation.NSOperationQueue
import ru.bartwell.kick.core.data.PlatformContext
import kotlin.math.sqrt

@OptIn(ExperimentalForeignApi::class)
public actual class LayoutTriggerController actual constructor(
    @Suppress("UNUSED_PARAMETER") context: PlatformContext,
    private val onTrigger: () -> Unit,
) {
    private val motionManager = CMMotionManager()

    public actual fun start(enabled: Boolean) {
        if (!enabled || !motionManager.accelerometerAvailable) return
        motionManager.accelerometerUpdateInterval = 0.1
        motionManager.startAccelerometerUpdatesToQueue(NSOperationQueue.mainQueue()) { data, _ ->
            val accel = data?.acceleration ?: return@startAccelerometerUpdatesToQueue
            accel.useContents {
                val value = sqrt(x * x + y * y + z * z)
                if (value > 3.0) {
                    onTrigger()
                }
            }
        }
    }

    public actual fun stop() {
        motionManager.stopAccelerometerUpdates()
    }
}
