package com.dialogapp.dialog.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dialogapp.dialog.model.LoggedInUser

@Dao
interface AccountDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(loggedInUser: LoggedInUser)

    @Query("DELETE FROM logged_in_user")
    fun deleteAccount()

    @Query("SELECT * FROM logged_in_user")
    fun loadAccount(): LoggedInUser?
}
