package com.csci448.jlwoolf.filepp

import android.R
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import com.csci448.jlwoolf.filepp.databinding.ActivityMainBinding
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import kotlin.random.Random


class MainActivity : AppCompatActivity(), ColorPickerDialogListener {
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

        fun randomColor(): Int {
            val rand = Random(System.currentTimeMillis())
            val randInts = List(3) { rand.nextInt(0, 255) }
            val red = randInts[0]
            val green = randInts[1]
            val blue = randInts[2]

            return 255 and 0xff shl 24 or (red and 0xff shl 16) or (green and 0xff shl 8) or (blue and 0xff)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(LOG_TAG, "onCreate() called")

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
    }

    override fun onStart() {
        super.onStart()
        Log.d(LOG_TAG, "onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(LOG_TAG, "onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(LOG_TAG, "onPause() called")

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
}