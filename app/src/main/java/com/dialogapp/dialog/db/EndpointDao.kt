package com.dialogapp.dialog.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dialogapp.dialog.model.EndpointData

@Dao
abstract class EndpointDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertEndpointData(endpointData: EndpointData)

    @Query("SELECT * FROM endpointData WHERE endpoint = :endpoint")
    abstract fun loadEndpointData(endpoint: String): LiveData<EndpointData>

    @Query("DELETE FROM endpointData")
    abstract fun clear()

    @Query("SELECT lastFetched FROM endpointData WHERE endpoint = :endpoint")
    abstract fun loadLastTimestamp(endpoint: String): Long
}