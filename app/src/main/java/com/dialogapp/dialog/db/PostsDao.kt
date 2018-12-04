package com.dialogapp.dialog.db

import androidx.paging.DataSource
import androidx.room.*
import com.dialogapp.dialog.model.Post

@Dao
abstract class PostsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertPosts(posts: List<Post>)

    @Query("SELECT * FROM posts WHERE endpoint = :endpoint ORDER BY datePublished DESC")
    abstract fun loadPostsByEndpoint(endpoint: String): DataSource.Factory<Int, Post>

    @Query("DELETE FROM posts WHERE endpoint = :endpoint")
    abstract fun deletePostsByEndpoint(endpoint: String)
}
