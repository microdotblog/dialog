package com.dialogapp.dialog.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.dialogapp.dialog.db.entity.Mention;
import com.dialogapp.dialog.db.entity.Timeline;
import com.dialogapp.dialog.model.Author;
import com.dialogapp.dialog.model.Item;
import com.dialogapp.dialog.model.Post;

import java.util.List;

@Dao
public interface PostsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertTimeline(List<Timeline> timelineIds);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertMentions(List<Mention> mentionIds);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAuthors(List<Author> authorList);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertPosts(List<Item> posts);

    @Query("SELECT id, url, contentHtml, datePublished, post_property_dateRelative, " +
            "post_property_isDeletable, post_property_isFavorite, author_name, " +
            "author_url, author_avatar_url, author_info_username FROM posts " +
            "INNER JOIN timeline ON timeline.timeline_post_id = posts.post_id " +
            "INNER JOIN authors ON posts.post_author_id = authors.author_id " +
            "ORDER BY datetime(datePublished) DESC")
    LiveData<List<Post>> loadTimeline();

    @Query("SELECT id, url, contentHtml, datePublished, post_property_dateRelative, " +
            "post_property_isDeletable, post_property_isFavorite, author_name, " +
            "author_url, author_avatar_url, author_info_username FROM posts " +
            "INNER JOIN mentions ON mentions.mentions_post_id = posts.post_id " +
            "INNER JOIN authors ON posts.post_author_id = authors.author_id " +
            "ORDER BY datetime(datePublished) DESC")
    LiveData<List<Post>> loadMentions();

    @Query("SELECT author_id FROM authors WHERE authors.author_info_username = :username")
    long getAuthorIdOf(String username);

    @Query("SELECT post_id FROM posts WHERE posts.url = :url")
    long getPostIdOf(String url);

    @Query("SELECT COUNT(*) from authors")
    int getAuthorsCount();

    @Query("SELECT COUNT(*) from posts")
    int getPostCount();

    @Query("SELECT COUNT(*) from timeline")
    int getTimelinePostCount();

    @Query("SELECT COUNT(*) from mentions")
    int getMentionsPostCount();
}
