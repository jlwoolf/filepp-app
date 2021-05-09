package com.csci448.jlwoolf.filepp.ui

import android.os.Bundle
import android.util.Log
import androidx.preference.PreferenceFragmentCompat
import com.csci448.jlwoolf.filepp.R

class SettingsFragment : PreferenceFragmentCompat() {

    companion object {
        private const val LOG_TAG = "448.SettingsFragment"
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        Log.d(LOG_TAG, "onCreatePreferences() called")
        setPreferencesFromResource(R.xml.settings, rootKey)
    }
}