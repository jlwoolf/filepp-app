package com.csci448.jlwoolf.filepp.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import com.csci448.jlwoolf.filepp.R
import com.csci448.jlwoolf.filepp.databinding.FragmentFileBinding
import java.io.File


class FileFragment : Fragment() {
    private var _binding: FragmentFileBinding? = null
    private val binding get() = _binding!!

    private val fileArgs: FileFragmentArgs by navArgs()
    private lateinit var file: File
    private lateinit var fileViewModel: FileViewModel

    private lateinit var sharedPreferences: SharedPreferences
    private var backgroundColor: Int = 0
    private var secondaryColor: Int = 0

    companion object {
        private const val LOG_TAG = "448.FileFragment"
    }

    private fun updateColors() {

        binding.fileLinearLayout.setBackgroundColor(backgroundColor)
        binding.filePathTextView.setBackgroundColor(backgroundColor)
        binding.fileTextView.setBackgroundColor(backgroundColor)
        requireActivity().window.statusBarColor = secondaryColor
        requireActivity().window.navigationBarColor = secondaryColor
        (activity as AppCompatActivity?)!!.supportActionBar?.setBackgroundDrawable(ColorDrawable(secondaryColor))
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(LOG_TAG, "onAttach() called")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(LOG_TAG, "onCreate() called")

        val typedValue = TypedValue()
        val theme = requireContext().theme
        theme.resolveAttribute(R.attr.defaultBackground, typedValue, true)
        backgroundColor = typedValue.data
        Log.d(LOG_TAG, "background color $backgroundColor")
        theme.resolveAttribute(R.attr.defaultSecondary, typedValue, true)
        secondaryColor = typedValue.data
        Log.d(LOG_TAG, "secondary color $secondaryColor")

        file = fileArgs.file
        setHasOptionsMenu(true)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        backgroundColor = sharedPreferences.getInt("background_color", backgroundColor)
        secondaryColor = sharedPreferences.getInt("secondary_color", secondaryColor)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        Log.d(LOG_TAG, "onCreateOptionsMenu() called")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(LOG_TAG, "onCreateView() called")

        _binding = FragmentFileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(LOG_TAG, "onViewCreated() called")
    }

    override fun onStart() {
        super.onStart()
        Log.d(LOG_TAG, "onStart() called")

        binding.fileTextView.text = file.name
        binding.filePathTextView.text = file.path
        binding.fileSizeTextView.text = "${file.length().toString()} bytes"
    }

    override fun onResume() {
        super.onResume()
        Log.d(LOG_TAG, "onResume() called")
        backgroundColor = sharedPreferences.getInt("background_color", backgroundColor)
        secondaryColor = sharedPreferences.getInt("secondary_color", secondaryColor)
        updateColors()
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
}