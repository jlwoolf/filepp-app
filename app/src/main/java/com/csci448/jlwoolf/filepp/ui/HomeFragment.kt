package com.csci448.jlwoolf.filepp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.csci448.jlwoolf.filepp.databinding.FragmentDirectoryBinding
import com.csci448.jlwoolf.filepp.databinding.FragmentHomeBinding
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
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
}