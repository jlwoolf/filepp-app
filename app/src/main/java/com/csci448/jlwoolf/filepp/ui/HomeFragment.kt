package com.csci448.jlwoolf.filepp.ui

import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.csci448.jlwoolf.filepp.MainActivity
import com.csci448.jlwoolf.filepp.R
import com.csci448.jlwoolf.filepp.databinding.FragmentHomeBinding
import java.io.File


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences

    private var readFilePermissionGrantedCallback: () -> Unit = {}
    private lateinit var readFilePermissionCallback: ActivityResultCallback<Boolean>
    private lateinit var readFilePermissionLauncher: ActivityResultLauncher<String>

    private var backgroundColor: Int = 0
    private var secondaryColor: Int = 0

    companion object {
        private const val LOG_TAG = "448.HomeFragment"
        private const val REQUIRED_READ_FILE_PERMISSION = android.Manifest.permission.READ_EXTERNAL_STORAGE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val typedValue = TypedValue()
        val theme = requireContext().theme
        theme.resolveAttribute(R.attr.defaultBackground, typedValue, true)
        backgroundColor = typedValue.data
        Log.d(LOG_TAG, "background color $backgroundColor")
        theme.resolveAttribute(R.attr.defaultSecondary, typedValue, true)
        secondaryColor = typedValue.data
        Log.d(LOG_TAG, "secondary color $secondaryColor")

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        backgroundColor = sharedPreferences.getInt("background_color", backgroundColor)
        secondaryColor = sharedPreferences.getInt("secondary_color", secondaryColor)

        readFilePermissionCallback = ActivityResultCallback { isGranted: Boolean ->
            if (isGranted)
                readFilePermissionGrantedCallback()
            else
                Toast.makeText(requireContext(), R.string.reason_for_permission, Toast.LENGTH_LONG).show()
            readFilePermissionGrantedCallback = {}
        }
        readFilePermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
            readFilePermissionCallback
        )
    }

    private fun updateColors() {
        binding.homeConstraintLayout.setBackgroundColor(backgroundColor)
        binding.homeGridLayout.setBackgroundColor(secondaryColor)
        binding.homeInternalStorageLayout.setBackgroundColor(secondaryColor)
        requireActivity().window.statusBarColor = secondaryColor
        requireActivity().window.navigationBarColor = secondaryColor
        (activity as AppCompatActivity?)!!.supportActionBar?.setBackgroundDrawable(ColorDrawable(secondaryColor))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        updateColors()
        return binding.root
    }

    private fun hasReadFilePermission() = ContextCompat.checkSelfPermission(
        requireContext(), REQUIRED_READ_FILE_PERMISSION
    ) == PackageManager.PERMISSION_GRANTED

    private fun attemptFileRead(file: File, callback: () -> Unit){
        val cb: () -> Unit = {
            if(file.canWrite() || file.exists()){
                file.createNewFile()
                callback()
            } else
                Toast.makeText(requireContext(),R.string.no_write_permission,Toast.LENGTH_SHORT).show()
        }
        if(hasReadFilePermission())
            cb()
        else{
            if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),REQUIRED_READ_FILE_PERMISSION))
                Toast.makeText(requireContext(),R.string.reason_for_permission,Toast.LENGTH_LONG).show()
            else{
                readFilePermissionGrantedCallback = cb
                readFilePermissionLauncher.launch(REQUIRED_READ_FILE_PERMISSION)
            }
        }
    }

    override fun onStart() {
        super.onStart()

        binding.homeInternalStorage.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToDirectoryFragment()
            attemptFileRead(File("/storage/self/primary")) { findNavController().navigate(action) }
        }

        binding.homeTopLeftButtton.setOnClickListener {
            val file = File("/storage/self/primary/Pictures")
            val action = HomeFragmentDirections.actionHomeFragmentToDirectoryFragment(file)
            attemptFileRead(file) { findNavController().navigate(action) }
        }
        binding.homeTopMiddleButton.setOnClickListener {
            val file = File("/storage/self/primary/Movies")
            val action = HomeFragmentDirections.actionHomeFragmentToDirectoryFragment(file)
            attemptFileRead(file) { findNavController().navigate(action) }
        }
        binding.homeTopRightButton.setOnClickListener {
            val file = File("/storage/self/primary/Music")
            val action = HomeFragmentDirections.actionHomeFragmentToDirectoryFragment(file)
            attemptFileRead(file) { findNavController().navigate(action) }
        }
        binding.homeBottomLeftButton.setOnClickListener {
            val file = File("/storage/self/primary/Documents")
            val action = HomeFragmentDirections.actionHomeFragmentToDirectoryFragment(file)
            attemptFileRead(file) { findNavController().navigate(action) }
        }
        binding.homeBottomMiddleButton.setOnClickListener {
            val file = File("/storage/self/primary/Download")
            val action = HomeFragmentDirections.actionHomeFragmentToDirectoryFragment(file)
            attemptFileRead(file) { findNavController().navigate(action) }
        }
        binding.homeBottomRightButton.setOnClickListener {
            val file = File("/storage/self/primary/Miscellaneous")
            val action = HomeFragmentDirections.actionHomeFragmentToDirectoryFragment(file)
            attemptFileRead(file) { findNavController().navigate(action) }
        }
    }

    override fun onResume() {
        super.onResume()
        backgroundColor = sharedPreferences.getInt("background_color", backgroundColor)
        secondaryColor = sharedPreferences.getInt("secondary_color", secondaryColor)
        updateColors()
    }
}