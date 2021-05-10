package com.csci448.jlwoolf.filepp.ui

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.Dialog
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.widget.Button
import androidx.annotation.StringRes
import androidx.core.content.FileProvider
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.preference.PreferenceManager
import com.csci448.jlwoolf.filepp.R
import com.csci448.jlwoolf.filepp.databinding.FragmentDirectorySettingsDialogBinding
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import java.io.File
import java.io.IOException
import java.util.*


class DirectorySettingsDialogFragment(
    private var name: String,
    private var background: Int,
    private var secondary: Int,
    private val callback: (name: String, background: Int, secondary: Int, reset: Boolean) -> Unit
) : DialogFragment(){
    private var reset: Boolean = false
    private lateinit var sharedPreferences: SharedPreferences
    companion object{
        private const val COLOR_PICKER_TAG = "DirectorySettingsDialogFragmentColorPicker"
    }

    private fun showColorPicker(@StringRes title: Int, color: Int, button: Button, consumer: (color: Int) -> Unit){
        ColorPickerDialog.newBuilder()
            .setDialogTitle(title).setColor(color)
            .create().apply {
                setColorPickerDialogListener(object : ColorPickerDialogListener{
                    override fun onColorSelected(id: Int, newColor: Int) {
                        if(color != newColor) {
                            reset = false
                        }
                        consumer(newColor)
                        button.setBackgroundColor(newColor)
                    }
                    override fun onDialogDismissed(id: Int) = Unit
                })
            }
            .show(parentFragmentManager,COLOR_PICKER_TAG)
    }

    override fun onCreateDialog(state: Bundle?): Dialog {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val binding = FragmentDirectorySettingsDialogBinding.inflate(LayoutInflater.from(context)).apply {
            directoryNameEditor.apply{
                addTextChangedListener{ watcher -> name = watcher.toString() }
                setText(name)
            }
            directoryBackgroundColorEditor.setOnClickListener { showColorPicker(R.string.background, background, directoryBackgroundColorEditor){ color -> background = color} }
            directoryBackgroundColorEditor.setBackgroundColor(background)
            directorySecondaryColorEditor.setOnClickListener { showColorPicker(R.string.secondary, secondary, directorySecondaryColorEditor) { color -> secondary = color} }
            directorySecondaryColorEditor.setBackgroundColor(secondary)

            directoryResetColor.setOnClickListener {
                reset = true
                background = sharedPreferences.getInt("background_color", 0)
                secondary = sharedPreferences.getInt("secondary_color", 0)
                directorySecondaryColorEditor.setBackgroundColor(secondary)
                directoryBackgroundColorEditor.setBackgroundColor(background)
            }
        }
        return activity?.let { activity ->
            // create a dialog to handle directory settings
            AlertDialog.Builder(activity)
                .setView(binding.root)
                .setMessage(getString(R.string.directory_settings))
                .setPositiveButton(getText(R.string.ok)){ _, _ -> callback(name,background,secondary,reset) }
                .setNegativeButton(getString(R.string.cancel)){ dialog, _ -> dialog.cancel() }
                .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }



}