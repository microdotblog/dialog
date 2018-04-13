package com.dialogapp.dialog.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.dialogapp.dialog.model.AccountResponse;
import com.dialogapp.dialog.model.Item;
import com.dialogapp.dialog.model.MicroBlogResponse;

@Database(entities = {AccountResponse.class, Item.class, MicroBlogResponse.class}, version = 1)
public abstract class MicroBlogDb extends RoomDatabase {
    public abstract AccountDao accountDao();

    public abstract PostsDao postsDao();
}
