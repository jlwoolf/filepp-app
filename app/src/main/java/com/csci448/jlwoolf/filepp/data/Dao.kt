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
    fun getData(): LiveData<List<Data>>

    @Query("SELECT * FROM data WHERE path=(:path)")
    fun getData(path: String): LiveData<Data?>

    @Update
    fun updateData(data: Data)

    @Insert
    fun addData(data: Data)

    @Delete
    fun removeData(data: Data)

    @Query("DELETE FROM data")
    fun clearData()
}