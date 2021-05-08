package com.csci448.jlwoolf.filepp.ui

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface.BUTTON_POSITIVE
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.DialogFragment
import com.csci448.jlwoolf.filepp.R
import com.csci448.jlwoolf.filepp.databinding.FragmentNewFolderDialogBinding.inflate
import java.io.File
import java.nio.file.Files

class NewFolderDialogFragment(private val parent: File, private val callback: Runnable = Runnable{}) : DialogFragment(){
    override fun onCreateDialog(state: Bundle?): Dialog {
        val binding = inflate(LayoutInflater.from(context))
        return activity?.let { activity ->
            // create a dialog to handle directory creation
            AlertDialog.Builder(activity)
                .setView(binding.root)
                .setMessage(R.string.new_folder)
                .setPositiveButton(R.string.ok) { _, _ -> }
                .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.cancel() }
            .create().apply {
                setOnShowListener {
                    // handle positive button directly, allowing for manual dismiss
                    getButton(BUTTON_POSITIVE).setOnClickListener { button: View? ->
                        val text = binding.newFolderDialogInput.text.toString()
                        val path = parent.toPath().resolve(text)
                        when {
                            text.isEmpty() -> Toast.makeText(activity,"Enter a directory name!",Toast.LENGTH_SHORT).show()
                            path.toFile().exists() -> Toast.makeText(activity,"Directory already exists!",Toast.LENGTH_SHORT).show()
                            else -> {
                                // request permission to write files if necessary
                                if(checkSelfPermission(context,WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED)
                                    ActivityCompat.requestPermissions(activity,arrayOf(WRITE_EXTERNAL_STORAGE),1)
                                // create directory if allowed
                                if(checkSelfPermission(context,WRITE_EXTERNAL_STORAGE) == PERMISSION_GRANTED){
                                    Files.createDirectory(path)
                                    callback.run()
                                }
                                // dismiss dialog
                                this.dismiss()
                            }
                        }
                    }
                }
            }
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}