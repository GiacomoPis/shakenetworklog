package it.icemangp.shakenetworklog.data

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

abstract class ShakeListener : SensorEventListener {

    private var acceleration = 10f
    private var currentAcceleration = SensorManager.GRAVITY_EARTH
    private var lastAcceleration = SensorManager.GRAVITY_EARTH
    
    private var lastDetectionTimeMillis = 0L
    private val shakeDetectionThresholdMillis = 2500
    private val accelerationThreshold = 12

    private var x = 0f
    private var y = 0f
    private var z = 0f

    private var currentTimeMillis = System.currentTimeMillis()

    private var deltaAcceleration = 0f

    override fun onSensorChanged(event: SensorEvent) {
        // Fetching x,y,z values
        x = event.values[0]
        y = event.values[1]
        z = event.values[2]
        lastAcceleration = currentAcceleration

        // Getting current accelerations with the help of fetched x,y,z values
        currentAcceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
        deltaAcceleration = currentAcceleration - lastAcceleration
        acceleration = acceleration * 0.9f + deltaAcceleration

        currentTimeMillis = System.currentTimeMillis()

        if (acceleration > accelerationThreshold && (currentTimeMillis - lastDetectionTimeMillis >= shakeDetectionThresholdMillis)) {
            onShake()
            lastDetectionTimeMillis = currentTimeMillis
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

    abstract fun onShake()
}