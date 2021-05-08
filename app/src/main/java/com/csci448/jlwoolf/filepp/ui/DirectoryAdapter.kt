package com.csci448.jlwoolf.filepp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.csci448.jlwoolf.filepp.databinding.FragmentItemBinding
import java.io.File

class DirectoryAdapter(private val files: List<FileItem?>,
                       private val clickListener: (FileItem) -> Unit)
    : RecyclerView.Adapter<DirectoryHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DirectoryHolder {
        val binding = FragmentItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return DirectoryHolder(binding)
    }

    override fun onBindViewHolder(holder: DirectoryHolder, position: Int) {
        val file = files[position]
        if (file != null) {
            holder.bind(file, clickListener)
        }
    }

    override fun getItemCount(): Int {
        return files.size
    }
}

