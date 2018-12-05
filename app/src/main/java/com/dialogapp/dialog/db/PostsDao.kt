package com.dialogapp.dialog.db

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

    @Query("SELECT * FROM posts WHERE belongsToEndpoint = :endpoint ORDER BY datePublished DESC")
    abstract fun loadPostsByEndpoint(endpoint: String): DataSource.Factory<Int, Post>

    @Query("DELETE FROM posts WHERE belongsToEndpoint = :endpoint")
    abstract fun deletePostsByEndpoint(endpoint: String)
}
