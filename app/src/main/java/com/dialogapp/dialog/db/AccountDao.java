package com.dialogapp.dialog.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.dialogapp.dialog.model.AccountResponse;

@Dao
public interface AccountDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AccountResponse accountResponse);

    @Query("SELECT * FROM account WHERE username = :username")
    LiveData<AccountResponse> fetchAccountInfo(String username);

    @Query("DELETE FROM account")
    void dropAccount();
}
