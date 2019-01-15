package com.dialogapp.dialog.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dialogapp.dialog.model.FollowingAccount

@Dao
abstract class FollowingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertFollowingAccounts(listFollowing: List<FollowingAccount>)

    @Query("SELECT * FROM following WHERE belongsToEndpoint = :endpoint ORDER BY name ASC")
    abstract fun loadFollowingAccounts(endpoint: String): LiveData<List<FollowingAccount>>

    @Query("DELETE FROM following")
    abstract fun clear()

    @Query("DELETE FROM following WHERE username = :username")
    abstract fun removeAccount(username: String)
}