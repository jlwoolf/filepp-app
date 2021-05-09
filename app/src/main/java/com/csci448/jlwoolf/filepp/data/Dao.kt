package com.csci448.jlwoolf.filepp.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import java.util.*

@Dao
interface Dao {
    @Query("SELECT * FROM data")
    fun getHistoryItems(): LiveData<List<Data>>

    @Query("SELECT * FROM data WHERE path=(:path)")
    fun getHistoryItem(path: String): LiveData<Data?>

    @Update
    fun updateHistoryItem(data: Data)

    @Insert
    fun addHistoryItem(data: Data)

    @Delete
    fun removeHistoryItem(data: Data)

    @Query("DELETE FROM data")
    fun clearHistoryItems()
}