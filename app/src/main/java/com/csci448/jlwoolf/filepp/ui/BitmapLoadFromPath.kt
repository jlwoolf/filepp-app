package com.csci448.jlwoolf.filepp.ui

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.util.Log
import androidx.core.graphics.scale
import java.util.concurrent.Callable
import kotlin.math.min


class BitmapLoadFromPath(private val path: String, private val x: Int, private val y: Int): Callable<Bitmap> {

    companion object {
        private const val LOG_TAG = "448.BitmapLoadFromPath"
        fun dpToPx(dp: Int): Int {
            val density: Float = Resources.getSystem()
                    .displayMetrics.density
            return Math.round(dp.toFloat() * density)
        }
        fun Bitmap.rotate(degrees: Float): Bitmap {
            val matrix = Matrix().apply { postRotate(degrees) }
            return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
        }
    }

    override fun call(): Bitmap {
        var bitmap: Bitmap = BitmapFactory.decodeFile(path)

        val exif = ExifInterface(path)
        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
        if(orientation == ExifInterface.ORIENTATION_ROTATE_90) {
            bitmap = bitmap.rotate(90f)
        }

        val minimum = min(bitmap.width, bitmap.height)
        val width = (bitmap.width * x)/minimum
        val height = (bitmap.height * y)/minimum
        Log.d(LOG_TAG, "width: $width, height: $height")
        bitmap = bitmap.scale(width, height, true)

        bitmap = Bitmap.createBitmap(
            bitmap,
            (bitmap.width - x) / 2,
            (bitmap.height - y) / 2,
            x,
            y
        )
        return bitmap
    }
}