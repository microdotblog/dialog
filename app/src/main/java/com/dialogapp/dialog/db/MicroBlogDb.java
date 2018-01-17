package com.dialogapp.dialog.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.dialogapp.dialog.model.AccountResponse;

@Database(entities = {AccountResponse.class}, version = 1)
public abstract class MicroBlogDb extends RoomDatabase {
    public abstract AccountDao accountDao();
}
