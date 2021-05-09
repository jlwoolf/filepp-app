package com.csci448.jlwoolf.filepp.ui

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.csci448.jlwoolf.filepp.R
import com.csci448.jlwoolf.filepp.databinding.FragmentDirectoryBinding
import java.io.File


class DirectoryFragment : Fragment() {
    private var _binding: FragmentDirectoryBinding? = null
    private val binding get() = _binding!!

    private val directoryArgs: DirectoryFragmentArgs by navArgs()
    private lateinit var storage: File

    private lateinit var directoryViewModel: DirectoryViewModel
    private lateinit var adapter: DirectoryAdapter

    private lateinit var readFilePermissionCallback: ActivityResultCallback<Boolean>
    private lateinit var readFilePermissionLauncher: ActivityResultLauncher<String>
    private lateinit var manageFilePermissionCallback: ActivityResultCallback<Boolean>
    private lateinit var managePermissionLauncher: ActivityResultLauncher<String>

    val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        // Handle the returned Uri
    }

    companion object {
        private const val LOG_TAG = "448.DirectoryFragment"
        private const val REQUIRED_READ_FILE_PERMISSION = android.Manifest.permission.READ_EXTERNAL_STORAGE
        private const val NEW_FOLDER = "NewFolder"
        private const val CHANNEL_ID = "fpp_channel"
    }

    private fun hasReadFilePermission() = ContextCompat.checkSelfPermission(
        requireContext(), REQUIRED_READ_FILE_PERMISSION
    ) == PackageManager.PERMISSION_GRANTED

    private fun updateUI(files: List<FileItem>) {
        // set up adapter to show files and manage file clicks
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(LOG_TAG, "onAttach() called")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(LOG_TAG, "onCreate() called")

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
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        Log.d(LOG_TAG, "onCreateOptionsMenu() called")
        inflater.inflate(R.menu.fragment_directory, menu)
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
        load()
    }

    override fun onPause() {
        super.onPause()
        Log.d(LOG_TAG, "onPause() called")
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
            R.id.new_folder_menu_item -> NewFolderDialogFragment(storage, this::load).show(
                childFragmentManager,
                NEW_FOLDER
            )
            R.id.menu_preferences -> {
                val action = DirectoryFragmentDirections.actionDirectoryFragmentToSettingsFragment()
                findNavController().navigate(action)
            }
            R.id.settings_menu_item -> {
                //ColorPickerDialog.newBuilder().show(requireActivity())
                getContent.launch("image/*")
            }
            R.id.menu_pin_notification -> {

            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }
}