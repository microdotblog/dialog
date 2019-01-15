package com.dialogapp.dialog.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dialogapp.dialog.model.EndpointData
import com.dialogapp.dialog.model.FollowingAccount
import com.dialogapp.dialog.model.Post

@Database(entities = [Post::class, EndpointData::class, FollowingAccount::class],
        version = 2, exportSchema = false)
abstract class InMemoryDb : RoomDatabase() {

    companion object {

        @Volatile
        private var INSTANCE: InMemoryDb? = null

        fun getInstance(context: Context): InMemoryDb? {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): InMemoryDb {
            return Room.inMemoryDatabaseBuilder(context, InMemoryDb::class.java)
                    .fallbackToDestructiveMigration()
                    .build()
        }
    }

    abstract fun posts(): PostsDao
    abstract fun endpointData(): EndpointDao
    abstract fun followingData(): FollowingDao
}