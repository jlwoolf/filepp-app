package com.csci448.jlwoolf.filepp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.GestureDetectorCompat
import androidx.navigation.NavDeepLinkBuilder
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import com.csci448.jlwoolf.filepp.databinding.ActivityMainBinding
import com.csci448.jlwoolf.filepp.ui.DirectoryFragment
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import java.io.File
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
        val rand = Random(System.currentTimeMillis())
        const val CHANNEL_ID = "fpp"

        fun randomColor(): Int {
            return Color.rgb(rand.nextInt(0,255), rand.nextInt(0,255), rand.nextInt(0,255))
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

        createNotificationChannel()
        createNotification()
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

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val channel = NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_MIN).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun createNotification() {
        val args: Bundle = Bundle()
        val path: String? = sharedPreferences.getString("pinned_path", null)
        if (path != null) {
            args.putSerializable("file", File(path))

            val pendingIntent = NavDeepLinkBuilder(this)
                .setComponentName(MainActivity::class.java)
                .setGraph(R.navigation.nav_graph)
                .setDestination(R.id.directoryFragment)
                .setArguments(args)
                .createPendingIntent()

            val builder = NotificationCompat.Builder(this, MainActivity.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_menu_delete)
                .setContentTitle("Pinned Directory")
                .setContentText("$path")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentIntent(pendingIntent)
                .setOngoing(true)

            with(NotificationManagerCompat.from(this)) {
                // notificationId is a unique int for each notification that you must define
                notify(0, builder.build())
            }
        } else {
            Log.d(LOG_TAG, "null")
        }
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }
}