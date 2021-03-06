package com.csci448.jlwoolf.filepp.ui

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.csci448.jlwoolf.filepp.R
import com.csci448.jlwoolf.filepp.data.Repository
import com.csci448.jlwoolf.filepp.databinding.FragmentDirectoryBinding
import com.jaredrummler.android.colorpicker.ColorPreference
import com.jaredrummler.android.colorpicker.ColorPreferenceCompat

class SettingsFragment : PreferenceFragmentCompat() {

    private var backgroundColor: Int = DEFAULT_COLOR
    private var secondaryColor: Int = DEFAULT_COLOR

    private lateinit var backgroundColorPreference: ColorPreferenceCompat
    private lateinit var secondaryColorPreference: ColorPreferenceCompat
    private lateinit var resetCustomizationPreference: Preference

    companion object {
        private const val LOG_TAG = "448.SettingsFragment"
        private const val DEFAULT_COLOR = 0
    }

    private fun updateColors() {
        view?.setBackgroundColor(backgroundColor)
        requireActivity().window.statusBarColor = secondaryColor
        requireActivity().window.navigationBarColor = secondaryColor
        (activity as AppCompatActivity?)!!.supportActionBar?.setBackgroundDrawable(ColorDrawable(secondaryColor))
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

        backgroundColor = preferenceManager.sharedPreferences.getInt("background_color", backgroundColor)
        secondaryColor = preferenceManager.sharedPreferences.getInt("secondary_color", secondaryColor)

        backgroundColorPreference = preferenceManager.findPreference("background_color")!!
        secondaryColorPreference = preferenceManager.findPreference("secondary_color")!!
        resetCustomizationPreference = preferenceManager.findPreference("reset_customization")!!

        backgroundColorPreference.setOnPreferenceChangeListener { _, newValue ->
            backgroundColor = newValue as Int
            updateColors()
            true
        }
        secondaryColorPreference.setOnPreferenceChangeListener { _, newValue ->
            secondaryColor = newValue as Int
            updateColors()
            true
        }
        resetCustomizationPreference.setOnPreferenceClickListener {
            Repository.getInstance(requireContext()).clearDatabase()

            theme.resolveAttribute(R.attr.defaultBackground, typedValue, true)
            backgroundColor = typedValue.data
            Log.d(LOG_TAG, "background color $backgroundColor")
            theme.resolveAttribute(R.attr.defaultSecondary, typedValue, true)
            secondaryColor = typedValue.data
            Log.d(LOG_TAG, "secondary color $secondaryColor")

            backgroundColorPreference.saveValue(backgroundColor)
            secondaryColorPreference.saveValue(secondaryColor)
            updateColors()
            true
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateColors()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        Log.d(LOG_TAG, "onCreatePreferences() called")
        setPreferencesFromResource(R.xml.settings, rootKey)
    }
}