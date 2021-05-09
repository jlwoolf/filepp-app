package com.csci448.jlwoolf.filepp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Data::class], version = 2)
//@TypeConverters(com.csci448.jlwoolf.filepp.data.TypeConverters::class)
abstract class Database : RoomDatabase() {
    abstract val dao: Dao

    companion object {
        private const val DATABASE_NAME = "history-database"
        private var INSTANCE: com.csci448.jlwoolf.filepp.data.Database? = null

        fun getInstance(context: Context): com.csci448.jlwoolf.filepp.data.Database {
            synchronized(this) {
                var instance = INSTANCE
                if(instance == null) {
                    instance = Room.databaseBuilder(context.applicationContext,
                        com.csci448.jlwoolf.filepp.data.Database::class.java,
                        DATABASE_NAME)
                        .build()
                }
                return instance
            }
        }
    }
}