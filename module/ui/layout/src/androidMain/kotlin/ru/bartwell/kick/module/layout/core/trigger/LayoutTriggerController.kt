package ru.bartwell.kick.module.layout.core.trigger

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.SystemClock
import ru.bartwell.kick.core.data.PlatformContext
import ru.bartwell.kick.core.data.get
import kotlin.math.sqrt

private const val LINEAR_ACCEL_THRESHOLD = 13f
private const val MIN_SHAKE_COUNT = 2
private const val SHAKE_WINDOW_MS = 600
private const val SHAKE_COOLDOWN_MS = 900
private const val GRAVITY_SIZE = 3
private const val ALPHA = 0.8f

private class ShakeListener(
    private val useLinearSensor: Boolean,
    private val onShake: () -> Unit,
    private val canTrigger: () -> Boolean
) : SensorEventListener {

    private val gravity = FloatArray(GRAVITY_SIZE)
    private val window = ArrayDeque<Long>()
    private var lastEmitAt = 0L

    override fun onSensorChanged(event: SensorEvent) {
        val now = SystemClock.elapsedRealtime()

        val (ax, ay, az) = if (useLinearSensor) {
            Triple(event.values[0], event.values[1], event.values[2])
        } else {
            gravity[0] = ALPHA * gravity[0] + (1 - ALPHA) * event.values[0]
            gravity[1] = ALPHA * gravity[1] + (1 - ALPHA) * event.values[1]
            gravity[2] = ALPHA * gravity[2] + (1 - ALPHA) * event.values[2]
            Triple(
                event.values[0] - gravity[0],
                event.values[1] - gravity[1],
                event.values[2] - gravity[2],
            )
        }

        val linearMag = sqrt(ax * ax + ay * ay + az * az)

        if (linearMag < LINEAR_ACCEL_THRESHOLD) return

        window.addLast(now)
        while (window.isNotEmpty() && now - window.first() > SHAKE_WINDOW_MS) {
            window.removeFirst()
        }

        if (window.size >= MIN_SHAKE_COUNT && now - lastEmitAt > SHAKE_COOLDOWN_MS) {
            lastEmitAt = now
            window.clear()
            if (canTrigger()) {
                onShake()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
}

public actual class LayoutTriggerController actual constructor(
    context: PlatformContext,
    private val onTrigger: () -> Unit,
) : BaseLayoutTriggerController(context, onTrigger) {
    private val ctx: Context = context.get()
    private var sensorManager: SensorManager? = null
    private var registeredSensor: Sensor? = null
    private var listener: SensorEventListener? = null

    public actual fun start(enabled: Boolean) {
        if (!enabled || sensorManager != null) return

        sensorManager = ctx.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        val sm = sensorManager ?: return

        val linear = sm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        val accel = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val sensor = linear ?: accel ?: return

        val useLinear = sensor.type == Sensor.TYPE_LINEAR_ACCELERATION
        val l = ShakeListener(useLinear, triggerCallback) { canTrigger() }
        listener = l
        registeredSensor = sensor

        sm.registerListener(l, sensor, SensorManager.SENSOR_DELAY_GAME)
    }

    public actual fun stop() {
        sensorManager?.unregisterListener(listener)
        listener = null
        registeredSensor = null
        sensorManager = null
    }
}
