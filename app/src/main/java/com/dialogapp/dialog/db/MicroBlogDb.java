package com.dialogapp.dialog.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.dialogapp.dialog.model.AccountResponse;
import com.dialogapp.dialog.model.Item;

@Database(entities = {AccountResponse.class, Item.class}, version = 1)
public abstract class MicroBlogDb extends RoomDatabase {
    public abstract AccountDao accountDao();

    public abstract PostsDao postsDao();
}
