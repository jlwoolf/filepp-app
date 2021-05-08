package com.csci448.jlwoolf.filepp.ui

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.TypedValue
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.csci448.jlwoolf.filepp.R
import com.csci448.jlwoolf.filepp.databinding.FragmentItemBinding
import java.io.File
import java.nio.file.Files


class DirectoryHolder(val binding: FragmentItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    private lateinit var file: File
    private val taskRunner = TaskRunner()

    companion object {
        private const val LOG_TAG = "448.DirectoryHolder"

        fun getResIdFromAttribute(context: Context, attr: Int): Int {
            if (attr == 0)
                return 0
            val typedValue = TypedValue()
            context.theme.resolveAttribute(attr, typedValue, true)
            return typedValue.resourceId
        }
    }

    fun bind(fileItem: FileItem) {
        file = fileItem.file
        if (!file.isDirectory) {
            binding.itemFragmentName.setTextColor(Color.RED)
        }

        val mimetype: String? = Files.probeContentType(file.toPath())
        if (mimetype != null && mimetype.split("/".toRegex()).toTypedArray()[0] == "image") {
            val drawableResId = getResIdFromAttribute(itemView.context, R.attr.ic_image)
            val drawable: Drawable? = ContextCompat.getDrawable(itemView.context, drawableResId)
            binding.itemFragmentIcon.background = drawable
            taskRunner.executeAsync(
                BitmapLoadFromPath(
                    this.file.path,
                    512,
                    512
                ),
                { bitmap: Bitmap ->
                    binding.itemFragmentIcon.background = BitmapDrawable(
                        Resources.getSystem(),
                        bitmap
                    )
                }
            )
        }

        binding.itemFragmentName.text = file.name
    }
}