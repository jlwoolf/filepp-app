package com.csci448.jlwoolf.filepp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.csci448.jlwoolf.filepp.databinding.FragmentDirectoryBinding
import com.csci448.jlwoolf.filepp.databinding.FragmentHomeBinding

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
    }
}