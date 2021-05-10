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
        private const val LOG_TAG = "448.Repository"
    }

    //two main functions that are used to read and write data
    //to the history database
    fun getData(): LiveData<List<Data>> = dao.getData()
    fun getData(path: String): LiveData<Data?> = dao.getData(path)

    fun addData(data: Data) {
        executor.execute {
            Log.d(LOG_TAG, "addData() called")
            dao.addData(data)
        }
    }
    //helper function to clear the database
    fun clearDatabase() {
        executor.execute {
            Log.d(LOG_TAG, "clearDatabase() called")
            dao.clearData()
        }
    }
    fun updateData(data: Data) {
        executor.execute {
            if(dao.getData(data.path).value == null) {
                addData(data)
            } else {
                Log.d(LOG_TAG, "updateData() called")
                dao.updateData(data)
            }
        }
    }

    fun getPinned(): LiveData<Data?> = dao.getPinned()
}