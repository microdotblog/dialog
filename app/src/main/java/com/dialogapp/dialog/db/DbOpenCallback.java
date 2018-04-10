package com.dialogapp.dialog.db;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.RoomDatabase;
import android.database.Cursor;
import android.support.annotation.NonNull;

public class DbOpenCallback extends RoomDatabase.Callback {
    @Override
    public void onOpen(@NonNull SupportSQLiteDatabase db) {
        super.onOpen(db);

        Runnable runnable = () -> {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

            String SQL_COUNT_ITEMS = "SELECT COUNT(*) FROM posts WHERE endpoint = ";
            String SQL_TRIM1 = "DELETE FROM posts WHERE id IN (SELECT id FROM posts WHERE endpoint = ";
            String SQL_TRIM2 = " ORDER BY datePublished ASC limit ";
            String SQL_TRIM3 = ")";

            db.beginTransaction();
            try {
                Cursor cursor1 = db.query(SQL_COUNT_ITEMS + "\"timeline\"");
                cursor1.moveToFirst();
                int count1 = cursor1.getInt(0);

                Cursor cursor2 = db.query(SQL_COUNT_ITEMS + "\"mentions\"");
                cursor2.moveToFirst();
                int count2 = cursor2.getInt(0);

                if (count1 >= 50)
                    db.execSQL(SQL_TRIM1 + "\"timeline\"" + SQL_TRIM2 + (count1 - 50) + SQL_TRIM3);
                if (count2 >= 50)
                    db.execSQL(SQL_TRIM1 + "\"mentions\"" + SQL_TRIM2 + (count2 - 50) + SQL_TRIM3);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        };
        runnable.run();
    }
}
