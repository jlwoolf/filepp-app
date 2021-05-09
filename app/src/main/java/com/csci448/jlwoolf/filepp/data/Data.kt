package com.csci448.jlwoolf.filepp.data

import android.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
class Data(@PrimaryKey val path: String,
           var backgroundColor: Int,
           var secondaryColor: Int,
           var textColor: Int,
           var imagePath: String) {
}