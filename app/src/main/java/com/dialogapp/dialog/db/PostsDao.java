package com.dialogapp.dialog.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.dialogapp.dialog.model.Item;
import com.dialogapp.dialog.model.MicroBlogResponse;
import com.dialogapp.dialog.ui.profilescreen.UserInfo;

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
    public abstract void dropPosts();

    @Query("DELETE FROM microblogData")
    public abstract void dropUsers();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertMicroblogData(MicroBlogResponse data);

    @Query("SELECT microblog_bio, author_author_name, author_author_url, author_author_avatar_url, " +
            "microblog_is_following, microblog_is_you, microblog_following_count " +
            "FROM microblogData WHERE microblog_username = :username")
    public abstract LiveData<UserInfo> loadUserData(String username);

    @Query("DELETE FROM posts WHERE endpoint NOT IN (\"timeline\", \"mentions\", \"favorites\")")
    public abstract void prunePosts();

    @Query("DELETE FROM microblogData")
    public abstract void pruneUserData();

    @Query("DELETE FROM posts WHERE id IN " +
            "(SELECT id FROM posts WHERE author_author_info_username = :username " +
            "AND endpoint = \"timeline\")")
    public abstract void deleteTimelinePostsOf(String username);

    @Query("UPDATE microblogData SET microblog_is_following = :follow WHERE microblog_username = :username")
    public abstract void updateFollowState(String username, boolean follow);

    @Query("UPDATE posts SET post_property_isFavorite = :favorite WHERE id = :id")
    public abstract void updateFavoriteState(String id, boolean favorite);

    @Query("DELETE FROM posts WHERE id = :id AND endpoint = \"favorites\"")
    public abstract void deleteFromFavorites(String id);
}
