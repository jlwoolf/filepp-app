package com.csci448.jlwoolf.filepp

import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import com.csci448.jlwoolf.filepp.databinding.ActivityMainBinding
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import kotlin.math.abs


class MainActivity : AppCompatActivity(), ColorPickerDialogListener, SensorEventListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private lateinit var sharedPreferences: SharedPreferences
    private var lastUpdate = System.currentTimeMillis()

    private var lastX = 0.0f
    private var lastY = 0.0f
    private var lastZ = 0.0f

    companion object {
        private const val LOG_TAG = "448.MainActivity"
        private const val SHAKE_THRESHOLD = 2500
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(LOG_TAG, "onCreate() called")

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
    }

    override fun onStart() {
        super.onStart()
        Log.d(LOG_TAG, "onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(LOG_TAG, "onResume() called")
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
    }

    override fun onPause() {
        super.onPause()
        Log.d(LOG_TAG, "onPause() called")
        sensorManager.unregisterListener(this)
    }

    override fun onStop() {
        super.onStop()
        Log.d(LOG_TAG, "onStop() called")
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Log.d(LOG_TAG, "onDestroy() called")
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(binding.navHostFragment.id).navigateUp()
                || super.onSupportNavigateUp()
    }

    override fun onColorSelected(dialogId: Int, color: Int) {
        Log.d(LOG_TAG, "$color")
    }

    override fun onDialogDismissed(dialogId: Int) {

    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val curTime = System.currentTimeMillis()
            if (curTime - lastUpdate > 100 && sharedPreferences.getBoolean("randomize_switch", true)) {
                val diffTime: Long = curTime - lastUpdate
                lastUpdate = curTime
                val x = event.values?.get(0) ?: 0.0f
                val y = event.values?.get(1) ?: 0.0f
                val z = event.values?.get(2) ?: 0.0f
                val speed: Float = abs(x + y + z - lastX - lastY - lastZ) / diffTime * 10000
                if (speed > SHAKE_THRESHOLD) {
                    Log.d("sensor", "shake detected w/ speed: $speed")
                    Toast.makeText(this, "shake detected w/ speed: $speed", Toast.LENGTH_SHORT)
                        .show()
                }
                lastX = x
                lastY = y
                lastZ = z
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {    }
}