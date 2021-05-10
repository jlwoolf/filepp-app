package com.csci448.jlwoolf.filepp.data

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.Dao
import java.util.*

@Dao
interface Dao {
    @Query("SELECT * FROM data")
    fun getData(): LiveData<List<Data>>

    @Query("SELECT * FROM data WHERE path=(:path)")
    fun getData(path: String): LiveData<Data?>

    @Update
    fun updateData(data: Data)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addData(data: Data)

    @Delete
    fun removeData(data: Data)

    @Query("DELETE FROM data")
    fun clearData()
}