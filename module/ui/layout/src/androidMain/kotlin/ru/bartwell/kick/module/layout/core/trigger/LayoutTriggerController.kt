package ru.bartwell.kick.module.layout.core.trigger

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.core.data.get
import kotlin.math.sqrt

private class ShakeListener(private val onShake: () -> Unit) : SensorEventListener {
    private var lastTime: Long = 0L

    override fun onSensorChanged(event: SensorEvent) {
        val x = event.values.getOrNull(0) ?: 0f
        val y = event.values.getOrNull(1) ?: 0f
        val z = event.values.getOrNull(2) ?: 0f
        val accel = sqrt(x * x + y * y + z * z)
        val now = System.currentTimeMillis()
        if (accel > 12 && now - lastTime > 500) {
            lastTime = now
            onShake()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}

public actual class LayoutTriggerController actual constructor(
    context: PlatformContext,
    private val onTrigger: () -> Unit,
) {
    private val ctx: Context = context.get()
    private var sensorManager: SensorManager? = null
    private val listener = ShakeListener { onTrigger() }

    public actual fun start(enabled: Boolean) {
        if (!enabled || sensorManager != null) return
        sensorManager = ctx.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if (accelerometer != null) {
            sensorManager?.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_UI)
        }
    }

    public actual fun stop() {
        sensorManager?.unregisterListener(listener)
        sensorManager = null
    }
}
