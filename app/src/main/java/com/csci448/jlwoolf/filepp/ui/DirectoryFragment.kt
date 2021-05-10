package com.csci448.jlwoolf.filepp.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDeepLinkBuilder
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.csci448.jlwoolf.filepp.MainActivity
import com.csci448.jlwoolf.filepp.R
import com.csci448.jlwoolf.filepp.data.Data
import com.csci448.jlwoolf.filepp.data.Repository
import com.csci448.jlwoolf.filepp.databinding.FragmentDirectoryBinding
import java.io.File
import kotlin.math.abs

class DirectoryFragment : Fragment(), SensorEventListener {
    private var _binding: FragmentDirectoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var repository: Repository

    private val directoryArgs: DirectoryFragmentArgs by navArgs()
    private lateinit var storage: File

    private lateinit var directoryViewModel: DirectoryViewModel
    private lateinit var adapter: DirectoryAdapter


    private lateinit var readFilePermissionCallback: ActivityResultCallback<Boolean>
    private lateinit var readFilePermissionLauncher: ActivityResultLauncher<String>
    private lateinit var manageFilePermissionCallback: ActivityResultCallback<Boolean>
    private lateinit var managePermissionLauncher: ActivityResultLauncher<String>

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor

    private var lastUpdate = System.currentTimeMillis()

    private var lastX = 0.0f
    private var lastY = 0.0f
    private var lastZ = 0.0f

    private var backgroundColor: Int = 0
    private var secondaryColor: Int = 0

    val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        // Handle the returned Uri
    }

    companion object {
        private const val LOG_TAG = "448.DirectoryFragment"
        private const val REQUIRED_READ_FILE_PERMISSION = android.Manifest.permission.READ_EXTERNAL_STORAGE
        private const val NEW_FOLDER = "NewFolder"
        private const val DIRECTORY_SETTINGS = "DirectorySettings"
        private const val CHANNEL_ID = "fpp_channel"
        private const val SHAKE_THRESHOLD = 2500
    }

    private fun hasReadFilePermission() = ContextCompat.checkSelfPermission(
        requireContext(), REQUIRED_READ_FILE_PERMISSION
    ) == PackageManager.PERMISSION_GRANTED

    private fun updateColors() {
        binding.directoryLinearLayout.setBackgroundColor(backgroundColor)
        binding.directoryTextView.setBackgroundColor(backgroundColor)
        requireActivity().window.statusBarColor = secondaryColor
        requireActivity().window.navigationBarColor = secondaryColor
        (activity as AppCompatActivity?)!!.supportActionBar?.setBackgroundDrawable(ColorDrawable(secondaryColor))
    }

    private fun updateUI(files: List<FileItem>) {
        // set up adapter to show files and manage file clicks
        //repository.getData(storage.path)
        DirectoryAdapter(files.sortedBy { it.file.name }) { fileItem: FileItem ->
            if(!fileItem.file.isDirectory) {
                val action = DirectoryFragmentDirections.actionDirectoryFragmentToFileFragment(
                    fileItem.file
                )
                findNavController().navigate(action)
            } else {
                val action = DirectoryFragmentDirections.actionDirectoryFragmentSelf(fileItem.file)
                findNavController().navigate(action)
            }
        }.apply {
            binding.directoryRecycleView.adapter = this
            adapter = this
        }

        // set parent path text
        val parents = storage.parent?.split("/") ?: listOf()
        binding.directoryPathTextView.text =
            if(parents.size <= 3)
                "/${parents.takeLast(parents.size).joinToString("/")}"
            else
                ".../${parents.takeLast(3).joinToString("/")}"
    }

    private fun load(){
        if(hasReadFilePermission()) {
            if (directoryArgs.file == null) {
                storage = File("/storage/self/primary")
                binding.directoryTextView.text = getString(R.string.internal_storage)
            } else {
                storage = directoryArgs.file!!
                if(storage.path == "/storage/self/primary") {
                    binding.directoryTextView.text = getString(R.string.internal_storage)
                } else {
                    binding.directoryTextView.text = storage.name
                }
                Log.d(LOG_TAG, storage.path)
            }

            val fileItemList = mutableListOf<FileItem>()
            storage.listFiles()!!.toList().forEach { fileItemList.add(FileItem(it)) }
            updateUI(fileItemList)
        }
    }

    private fun applySettings(name: String, background: Int, secondary: Int, icon: Int){
        Log.d(LOG_TAG,"Applying Directory Settings: name=$name, background=$background, secondary=$secondary, icon=$icon")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        Log.d(LOG_TAG, "onAttach() called")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(LOG_TAG, "onCreate() called")

        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        val factory = DirectoryViewModelFactory()
        directoryViewModel = ViewModelProvider(this@DirectoryFragment, factory)
            .get(DirectoryViewModel::class.java)

        readFilePermissionCallback = ActivityResultCallback { isGranted: Boolean -> if (isGranted) {
                Log.d(LOG_TAG, "permission granted!")
            } else {
                Log.d(LOG_TAG, "permission denied")
                Toast.makeText(requireContext(), R.string.reason_for_permission, Toast.LENGTH_LONG).show()
            }
        }
        readFilePermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
            readFilePermissionCallback
        )

        setHasOptionsMenu(true)
        repository = Repository.getInstance(requireContext())
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        backgroundColor = sharedPreferences.getInt("background_color", 0)
        secondaryColor = sharedPreferences.getInt("secondary_color", 0)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        Log.d(LOG_TAG, "onCreateOptionsMenu() called")
        inflater.inflate(R.menu.fragment_directory, menu)

        if(sharedPreferences.getString("pinned_path", null) == storage.path) {
            menu.getItem(2).isChecked = true
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(LOG_TAG, "onCreateView() called")

        _binding = FragmentDirectoryBinding.inflate(inflater, container, false)
        binding.directoryRecycleView.layoutManager = LinearLayoutManager(context)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(LOG_TAG, "onViewCreated() called")
    }

    override fun onStart() {
        super.onStart()
        Log.d(LOG_TAG, "onStart() called")

        if(!hasReadFilePermission()) {
            Log.d(LOG_TAG, "user has NOT granted permission to read files")
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    REQUIRED_READ_FILE_PERMISSION
                )
            ) {
                Log.d(LOG_TAG, "show an explanation")
                Toast.makeText(
                    requireContext(),
                    R.string.reason_for_permission,
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Log.d(LOG_TAG, "no explanation needed, request permission")
                readFilePermissionLauncher.launch(REQUIRED_READ_FILE_PERMISSION)
            }

        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(LOG_TAG, "onResume() called")

        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)

        Log.d(LOG_TAG, "${sharedPreferences.getInt("background_color", 0)}")

        load()
        repository.getData(storage.path).observe(
            viewLifecycleOwner,
            Observer { data ->
                data?.let {
                    this.backgroundColor = data.backgroundColor
                    this.secondaryColor = data.secondaryColor
                    updateColors()
                }
            }
        )

        updateColors()
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

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(LOG_TAG, "onDestroyView() called")

        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(LOG_TAG, "onDestroy() called")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(LOG_TAG, "onDetach() called")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.new_folder_menu_item -> NewFolderDialogFragment(storage, this::load).show(childFragmentManager,NEW_FOLDER)
            R.id.menu_preferences -> {
                val action = DirectoryFragmentDirections.actionDirectoryFragmentToSettingsFragment()
                findNavController().navigate(action)
            }
            R.id.settings_menu_item -> {
                DirectorySettingsDialogFragment(
                    storage.name,
                    0, // todo get background color
                    0, // todo get secondary color
                    0, // todo get icon id
                    this::applySettings
                ).show(childFragmentManager,DIRECTORY_SETTINGS)
            }
            R.id.menu_pin_notification -> {
                Log.d(LOG_TAG, "checking notification")
                item.isChecked = !item.isChecked
                if(!item.isChecked) {
                    sharedPreferences.edit().putString("pinned_path", null).apply()
                } else {
                    sharedPreferences.edit().putString("pinned_path", storage.path).apply()
                    createNotification()
                }
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
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
                    Log.d(LOG_TAG, "shake detected w/ speed: $speed")
                    backgroundColor = MainActivity.randomColor()
                    secondaryColor = MainActivity.randomColor()
                    updateColors()

                    repository.addData(Data(
                        path = storage.path,
                        backgroundColor = backgroundColor,
                        secondaryColor = secondaryColor,
                        imagePath = ".")
                    )
                }
                lastX = x
                lastY = y
                lastZ = z
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    fun createNotification() {
        val args: Bundle = Bundle()
        val path = sharedPreferences.getString("pinned_path", "/storage/self/primary")
        if(path != null) {
            args.putSerializable("file", File(path))

            val pendingIntent = NavDeepLinkBuilder(requireActivity())
                .setComponentName(MainActivity::class.java)
                .setGraph(R.navigation.nav_graph)
                .setDestination(R.id.directoryFragment)
                .setArguments(args)
                .createPendingIntent()

            val builder = NotificationCompat.Builder(requireActivity(), MainActivity.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_menu_delete)
                .setContentTitle("Pinned Directory")
                .setContentText("$path")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentIntent(pendingIntent)
                .setOngoing(true)

            with(NotificationManagerCompat.from(requireActivity())) {
                // notificationId is a unique int for each notification that you must define
                notify(0, builder.build())
            }
        }

    }
}