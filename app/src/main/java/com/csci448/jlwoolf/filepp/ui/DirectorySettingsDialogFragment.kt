package com.csci448.jlwoolf.filepp.ui

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.annotation.StringRes
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import com.csci448.jlwoolf.filepp.R
import com.csci448.jlwoolf.filepp.databinding.FragmentDirectorySettingsDialogBinding
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener

class DirectorySettingsDialogFragment(
    private var name: String,
    private var background: Int,
    private var secondary: Int,
    private var icon: Int,
    private val callback: (name: String, background: Int, secondary: Int, icon: Int) -> Unit
) : DialogFragment(){

    companion object{
        private const val COLOR_PICKER_TAG = "DirectorySettingsDialogFragmentColorPicker"
    }

    private fun showColorPicker(@StringRes title: Int, consumer: (color: Int) -> Unit){
        ColorPickerDialog.newBuilder()
            .setDialogTitle(title)
            .create().apply {
                setColorPickerDialogListener(object : ColorPickerDialogListener{
                    override fun onColorSelected(id: Int, color: Int) { consumer(color) }
                    override fun onDialogDismissed(id: Int) = Unit
                })
            }.show(parentFragmentManager,COLOR_PICKER_TAG)
    }

    override fun onCreateDialog(state: Bundle?): Dialog {
        val binding = FragmentDirectorySettingsDialogBinding.inflate(LayoutInflater.from(context)).apply {
            directoryNameEditor.apply{
                addTextChangedListener{ watcher -> name = watcher.toString() }
                setText(name)
            }
            directoryBackgroundColorEditor.setOnClickListener { showColorPicker(R.string.background){ color -> background = color} }
            directorySecondaryColorEditor.setOnClickListener { showColorPicker(R.string.secondary){ color -> secondary = color} }
            directoryIconEditor.apply {
                setOnClickListener {  } // todo
                setImageResource(icon)
            }
        }
        return activity?.let { activity ->
            // create a dialog to handle directory settings
            AlertDialog.Builder(activity)
                .setView(binding.root)
                .setMessage(getString(R.string.directory_settings))
                .setPositiveButton(getText(R.string.ok)){ _, _ -> callback(name,background,secondary,icon) }
                .setNegativeButton(getString(R.string.cancel)){ dialog, _ -> dialog.cancel() }
                .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}