package com.dialogapp.dialog.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.dialogapp.dialog.model.Item;
import com.dialogapp.dialog.model.MicroBlogResponse;

import java.util.List;

@Dao
public interface PostsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPosts(List<Item> posts);

    @Query("SELECT * FROM posts WHERE endpoint = :endpoint ORDER BY datePublished DESC")
    LiveData<List<Item>> loadEndpoint(String endpoint);

    @Query("DELETE FROM posts WHERE endpoint = :endpoint")
    void deletePosts(String endpoint);

    @Query("DELETE FROM posts")
    void dropTable();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMicroblogData(MicroBlogResponse data);

    @Query("SELECT * FROM microblogData WHERE microblog_username = :username")
    LiveData<MicroBlogResponse> loadMicroblogData(String username);

    @Query("SELECT DISTINCT id FROM posts WHERE author_author_info_username = :username AND " +
            "datePublished = :datePublished AND endpoint != \"favorites\"")
    String getActualIdOfFavoritePost(String username, String datePublished);
}
