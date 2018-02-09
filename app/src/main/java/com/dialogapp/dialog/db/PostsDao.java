package com.dialogapp.dialog.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.dialogapp.dialog.model.Item;

import java.util.List;

@Dao
public interface PostsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertPosts(List<Item> posts);

    @Query("SELECT * FROM posts WHERE endpoint = :endpoint")
    LiveData<List<Item>> loadEndpoint(String endpoint);

    @Query("DELETE FROM posts WHERE endpoint = :endpoint")
    void deletePosts(String endpoint);
}
