package com.dialogapp.dialog.db

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import com.dialogapp.dialog.model.Post
import com.dialogapp.dialog.vo.FAVORITES

@Dao
abstract class PostsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertPosts(posts: List<Post>)

    @Query("SELECT * FROM posts WHERE belongsToEndpoint = :endpoint ORDER BY datetime(datePublished) DESC")
    abstract fun loadPostsByEndpointAsDataSource(endpoint: String): DataSource.Factory<Int, Post>

    @Query("SELECT * FROM posts WHERE belongsToEndpoint = :endpoint ORDER BY datetime(datePublished) DESC")
    abstract fun loadPostsByEndpointAsLiveData(endpoint: String): LiveData<List<Post>>

    @Query("DELETE FROM posts WHERE belongsToEndpoint = :endpoint")
    abstract fun deletePostsByEndpoint(endpoint: String)

    @Query("DELETE FROM posts")
    abstract fun clear()

    @Query("UPDATE posts SET post_property_isFavorite = :state WHERE id = :postId")
    abstract fun updateFavoriteState(postId: String, state: Boolean)

    @Query("SELECT post_property_isFavorite FROM posts WHERE id = :postId AND belongsToEndpoint = :endpoint")
    abstract fun getFavoriteState(endpoint: String, postId: String): Boolean

    @Query("SELECT author_author_info_username FROM posts WHERE belongsToEndpoint = :endpoint " +
            "AND id = :postId")
    abstract fun getUsername(endpoint: String, postId: String): String

    @Query("SELECT datePublished FROM posts WHERE belongsToEndpoint = :endpoint " +
            "AND id = :postId")
    abstract fun getDate(endpoint: String, postId: String): String

    @Transaction
    open fun updateAndRemoveGivenFavId(favId: String, state: Boolean) {
        updateFavoriteStatesGivenFavId(endpoint = FAVORITES, favId = favId, state = false)
        removeFromFavorites(endpoint = FAVORITES, favId = favId)
    }

    @Transaction
    open fun updateAndRemoveFromFav(username: String, datePublished: String, postId: String,
                                    state: Boolean) {
        updateFavoriteState(postId = postId, state = false)
        removeFromFavoritesGiven(username = username, datePublished = datePublished, endpoint = FAVORITES)
    }

    @Query("UPDATE posts SET post_property_isFavorite = :state WHERE author_author_info_username = :username " +
            "AND datePublished = :datePublished")
    abstract fun updateFavoriteStatesGiven(username: String, datePublished: String, state: Boolean)

    @Query("UPDATE posts SET post_property_isFavorite = :state WHERE id IN (SELECT p1.id FROM " +
            "posts p1 INNER JOIN posts p2 ON p1.author_author_info_username = p2.author_author_info_username " +
            "AND p1.datePublished = p2.datePublished WHERE p2.id = :favId AND p2.belongsToEndpoint = :endpoint)")
    protected abstract fun updateFavoriteStatesGivenFavId(endpoint: String, favId: String, state: Boolean)

    @Query("DELETE FROM posts WHERE id = :favId AND belongsToEndpoint = :endpoint")
    protected abstract fun removeFromFavorites(endpoint: String, favId: String)

    @Query("DELETE FROM posts WHERE author_author_info_username = :username AND " +
            "datePublished = :datePublished AND belongsToEndpoint = :endpoint")
    protected abstract fun removeFromFavoritesGiven(username: String, datePublished: String,
                                                    endpoint: String)
    @Query("DELETE FROM posts WHERE id = :postId")
    abstract fun deletePost(postId: String)
}
