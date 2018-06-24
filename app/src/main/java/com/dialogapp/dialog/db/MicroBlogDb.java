package com.dialogapp.dialog.db;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;

import com.dialogapp.dialog.model.AccountResponse;
import com.dialogapp.dialog.model.Item;
import com.dialogapp.dialog.model.MicroBlogResponse;

@Database(entities = {AccountResponse.class, Item.class, MicroBlogResponse.class}, version = 1,
        exportSchema = false)
public abstract class MicroBlogDb extends RoomDatabase {
    public abstract AccountDao accountDao();

    public abstract PostsDao postsDao();

    static MicroBlogDb INSTANCE;

    public static MicroBlogDb getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (MicroBlogDb.class) {
                if (INSTANCE == null) {
                    INSTANCE = buildDatabase(context);
                }
            }
        }
        return INSTANCE;
    }

    private static MicroBlogDb buildDatabase(Context context) {
        return Room.databaseBuilder(context, MicroBlogDb.class, "microblog.db")
                .addCallback(new Callback() {
                    @Override
                    public void onOpen(@NonNull SupportSQLiteDatabase db) {
                        super.onOpen(db);

                        new Thread(() -> {
                            MicroBlogDb microBlogDb = getInstance(context);
                            microBlogDb.beginTransaction();
                            try {
                                microBlogDb.postsDao().pruneUserData();
                                microBlogDb.postsDao().prunePosts();
                                microBlogDb.setTransactionSuccessful();
                            } finally {
                                microBlogDb.endTransaction();
                            }
                        }).start();
                    }
                }).build();
    }
}
