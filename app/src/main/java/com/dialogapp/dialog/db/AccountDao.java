package com.dialogapp.dialog.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

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
