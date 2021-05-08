package com.csci448.jlwoolf.filepp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.csci448.jlwoolf.filepp.databinding.FragmentItemBinding

class DirectoryAdapter(private val files: List<FileItem>,
                       private val clickListener: (FileItem) -> Unit) : RecyclerView.Adapter<DirectoryHolder>() {

    private val selected: MutableSet<FileItem> = mutableSetOf()
    private val holders: MutableList<DirectoryHolder> = mutableListOf()
    private val multiselect: Boolean get() = selected.isNotEmpty()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DirectoryHolder {
        val binding = FragmentItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return DirectoryHolder(binding)
    }

    private fun toggle(holder: DirectoryHolder, item: FileItem){
        holder.binding.itemFragmentCheckbox.apply { isChecked = !isChecked }
        item.selected = !item.selected
        if(selected.contains(item))
            selected.remove(item)
        else
            selected.add(item)
        holders.stream().forEach{ it.binding.itemFragmentCheckbox.isVisible = multiselect }
    }

    override fun onBindViewHolder(holder: DirectoryHolder, position: Int) {
        val file = files[position]
        holder.bind(file)
        holders.add(holder)

        // multiselect support
        holder.itemView.apply {
            setOnLongClickListener {
                toggle(holder,file)
                true
            }
            setOnClickListener {
                if(multiselect)
                    toggle(holder,file)
                else
                    clickListener(file)
            }
        }
    }

    override fun getItemCount(): Int {
        return files.size
    }
}

