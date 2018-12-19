package com.dialogapp.dialog.db

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import com.dialogapp.dialog.model.EndpointData
import com.dialogapp.dialog.model.Post

@Dao
abstract class PostsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertPosts(posts: List<Post>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertEndpointData(endpointData: EndpointData)

    @Query("SELECT * FROM posts WHERE belongsToEndpoint = :endpoint ORDER BY datetime(datePublished) DESC")
    abstract fun loadPostsByEndpoint(endpoint: String): DataSource.Factory<Int, Post>

    @Query("SELECT * FROM endpointData WHERE endpoint = :endpoint")
    abstract fun loadEndpointData(endpoint: String): LiveData<EndpointData>

    @Query("DELETE FROM posts WHERE belongsToEndpoint = :endpoint")
    abstract fun deletePostsByEndpoint(endpoint: String)

    @Query("DELETE FROM posts")
    abstract fun clearPosts()

    @Query("DELETE FROM endpointData")
    abstract fun clearEndpointData()

    @Transaction
    open fun clearAll() {
        clearPosts()
        clearEndpointData()
    }

    @Query("SELECt lastFetched FROM endpointData WHERE endpoint = :endpoint")
    abstract fun fetchLastTimestamp(endpoint: String): Long
}
