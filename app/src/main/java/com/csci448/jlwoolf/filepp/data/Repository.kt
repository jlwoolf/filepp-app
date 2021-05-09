package com.csci448.jlwoolf.filepp.data

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import java.util.concurrent.Executors

class Repository private constructor(private val dao: Dao){
    private val executor = Executors.newSingleThreadExecutor()
    companion object {
        private var INSTANCE: Repository? = null
        fun getInstance(context: Context): Repository {
            synchronized(this) {
                var instance = INSTANCE
                if(instance == null) {
                    val database = Database.getInstance(context)
                    instance = Repository(database.dao)
                    INSTANCE = instance
                }
                return instance
            }
        }
        private const val LOG_TAG = "TTT.HistoryRepository"
    }

    //two main functions that are used to read and write data
    //to the history database
    fun getHistoryItems(): LiveData<List<Data>> = dao.getHistoryItems()

    fun addHistoryItem(data: Data) {
        executor.execute {
            Log.d(LOG_TAG, "addHistoryItem() called")
            dao.addHistoryItem(data)
        }
    }
    //helper function to clear the database
    fun clearDatabase() {
        executor.execute {
            Log.d(LOG_TAG, "clearDatabase() called")
            dao.clearHistoryItems()
        }
    }
}