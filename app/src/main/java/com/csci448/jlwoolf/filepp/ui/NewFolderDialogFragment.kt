package com.csci448.jlwoolf.filepp.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.csci448.jlwoolf.filepp.R
import com.csci448.jlwoolf.filepp.databinding.FragmentDirectoryBinding
import com.csci448.jlwoolf.filepp.databinding.FragmentNewFolderDialogBinding

class NewFolderDialogFragment : DialogFragment() {
    private var _binding: FragmentNewFolderDialogBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance(): NewFolderDialogFragment {
            return NewFolderDialogFragment().apply {  }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater

            builder.setView(inflater.inflate(R.layout.fragment_new_folder_dialog, null))
                .setMessage(R.string.new_folder)
                .setPositiveButton(R.string.ok,
                    DialogInterface.OnClickListener { dialog, id ->
                        // FIRE ZE MISSILES!
                    })
                .setNegativeButton(R.string.cancel,
                    DialogInterface.OnClickListener { dialog, id ->
                        // User cancelled the dialog
                    })
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}