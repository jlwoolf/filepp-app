package com.csci448.jlwoolf.filepp.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.csci448.jlwoolf.filepp.databinding.FragmentItemBinding

class DirectoryAdapter(private val files: List<FileItem>,
                       private val clickListener: (FileItem) -> Unit) : RecyclerView.Adapter<DirectoryHolder>() {

    companion object {
        private const val LOG_TAG = "448.DirectoryAdapter"
    }

    private val selected: MutableSet<FileItem> = mutableSetOf()
    private val holders: MutableMap<FileItem, DirectoryHolder> = mutableMapOf()
    private var multiselect: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DirectoryHolder {
        val binding = FragmentItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return DirectoryHolder(binding)
    }

    private fun toggle(item: FileItem){
        item.selected = !item.selected

        if(item.selected) {
            selected.add(item)
        } else {
            selected.remove(item)
        }

        Log.d(LOG_TAG, "selected count = ${selected.size}")

        if(selected.isEmpty()) {
            multiselect = false
        }

        holders.forEach { it.value.binding.itemFragmentCheckbox.isVisible = multiselect }
    }

    override fun onBindViewHolder(holder: DirectoryHolder, position: Int) {
        val file = files[position]
        holder.bind(file)

        holders[file] = holder
        Log.d(LOG_TAG, "holder count = ${holders.size}, item count = ${files.size}")
        holder.binding.itemFragmentCheckbox.isVisible = multiselect

        // multiselect support
        holder.itemView.apply {
            setOnLongClickListener {
                if (!multiselect) {
                    multiselect = true
                    holder.binding.itemFragmentCheckbox.apply { isChecked = !isChecked }
                }

                true
            }

            setOnClickListener {
                if(multiselect)
                    holder.binding.itemFragmentCheckbox.apply { isChecked = !isChecked }
                else
                    clickListener(file)
            }
        }

        holder.binding.itemFragmentCheckbox.setOnCheckedChangeListener { _, _ ->
            toggle(file)
        }
    }

    override fun getItemCount(): Int {
        return files.size
    }
}

