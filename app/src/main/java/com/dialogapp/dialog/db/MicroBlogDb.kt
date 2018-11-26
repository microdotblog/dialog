package com.dialogapp.dialog.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.dialogapp.dialog.model.LoggedInUser
import com.dialogapp.dialog.model.MicroBlogResponse
import com.dialogapp.dialog.model.Post

@Database(entities = [LoggedInUser::class, Post::class, MicroBlogResponse::class],
        version = 1, exportSchema = false)
abstract class MicroBlogDb : RoomDatabase() {
    abstract fun accountDao(): AccountDao

    companion object {

        private var INSTANCE: MicroBlogDb? = null

        fun getInstance(context: Context): MicroBlogDb? {
            if (INSTANCE == null) {
                synchronized(MicroBlogDb::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = buildDatabase(context)
                    }
                }
            }
            return INSTANCE
        }

        private fun buildDatabase(context: Context): MicroBlogDb {
            return Room.databaseBuilder(context, MicroBlogDb::class.java, "microblog.db")
                    .fallbackToDestructiveMigration()
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)

                            Thread {
                                val microBlogDb = getInstance(context)
                                microBlogDb!!.beginTransaction()
                                try {
                                    microBlogDb.setTransactionSuccessful()
                                } finally {
                                    microBlogDb.endTransaction()
                                }
                            }.start()
                        }
                    }).build()
        }
    }
}
