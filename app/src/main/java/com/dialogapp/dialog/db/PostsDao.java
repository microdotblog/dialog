package com.dialogapp.dialog.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.dialogapp.dialog.model.Item;
import com.dialogapp.dialog.model.MicroBlogResponse;

import java.util.List;

@Dao
public abstract class PostsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertPosts(List<Item> posts);

    @Transaction
    public void deleteAndInsertPostsInTransaction(String endpoint, List<Item> posts) {
        deletePosts(endpoint);
        insertPosts(posts);
    }

    @Query("SELECT * FROM posts WHERE endpoint = :endpoint ORDER BY datePublished DESC")
    public abstract LiveData<List<Item>> loadEndpoint(String endpoint);

    @Query("DELETE FROM posts WHERE endpoint = :endpoint")
    public abstract void deletePosts(String endpoint);

    @Query("DELETE FROM posts")
    public abstract void dropTable();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertMicroblogData(MicroBlogResponse data);

    @Query("SELECT * FROM microblogData WHERE microblog_username = :username")
    public abstract LiveData<MicroBlogResponse> loadMicroblogData(String username);

    @Query("SELECT DISTINCT id FROM posts WHERE author_author_info_username = :username AND " +
            "datePublished = :datePublished AND endpoint != \"favorites\"")
    public abstract String getActualIdOfFavoritePost(String username, String datePublished);

    @Query("DELETE FROM posts WHERE endpoint NOT IN (\"timeline\", \"mentions\", \"discover\", \"favorites\")")
    public abstract void prunePosts();

    @Query("DELETE FROM microblogData WHERE microblog_username != :username")
    public abstract void pruneUserData(String username);
}
