package com.dialogapp.dialog.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {}, version = 1)
public abstract class MicroBlogDb extends RoomDatabase {
}
