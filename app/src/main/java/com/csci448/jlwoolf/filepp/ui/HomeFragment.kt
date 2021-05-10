package com.csci448.jlwoolf.filepp.ui

import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.csci448.jlwoolf.filepp.R
import com.csci448.jlwoolf.filepp.databinding.FragmentHomeBinding
import java.io.File

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences

    private var backgroundColor: Int = 0
    private var secondaryColor: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        backgroundColor = sharedPreferences.getInt("background_color", 0)
        secondaryColor = sharedPreferences.getInt("secondary_color", 0)
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

    override fun onStart() {
        super.onStart()
        binding.homeInternalStorage.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToDirectoryFragment()
            findNavController().navigate(action)
        }

        binding.homeTopLeftButtton.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToDirectoryFragment(File("/storage/self/primary/Pictures"))
            findNavController().navigate(action)
        }
        binding.homeTopMiddleButton.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToDirectoryFragment(File("/storage/self/primary/Movies"))
            findNavController().navigate(action)
        }
        binding.homeTopRightButton.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToDirectoryFragment(File("/storage/self/primary/Music"))
            findNavController().navigate(action)
        }
        binding.homeBottomLeftButton.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToDirectoryFragment(File("/storage/self/primary"))
            findNavController().navigate(action)
        }
        binding.homeBottomMiddleButton.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToDirectoryFragment(File("/storage/self/primary/Download"))
            findNavController().navigate(action)
        }
        binding.homeBottomRightButton.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToDirectoryFragment(File("/storage/self/primary"))
            findNavController().navigate(action)
        }
    }

    override fun onResume() {
        super.onResume()
        backgroundColor = sharedPreferences.getInt("background_color", 0)
        secondaryColor = sharedPreferences.getInt("secondary_color", 0)
        updateColors()
    }
}