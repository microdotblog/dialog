package com.dialogapp.dialog.model;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dialogapp.dialog.db.ListTypeConverters;
import com.squareup.moshi.Json;

import java.util.List;

@Entity(tableName = "microblogData", primaryKeys = "homePageUrl")
@TypeConverters(ListTypeConverters.class)
public class MicroBlogResponse {

    @Json(name = "title")
    public final String title;
    @NonNull
    @Json(name = "home_page_url")
    public final String homePageUrl;
    @Json(name = "feed_url")
    public final String feedUrl;
    @Json(name = "_microblog")
    @Embedded(prefix = "microblog_")
    public final Microblog microblog;
    @Nullable
    @Json(name = "author")
    @Embedded(prefix = "author_")
    public final Author author;
    @Json(name = "items")
    public final List<Item> items;

    public MicroBlogResponse(String title, String homePageUrl, String feedUrl, Microblog microblog,
                             @Nullable Author author, List<Item> items) {
        this.title = title;
        this.homePageUrl = homePageUrl;
        this.feedUrl = feedUrl;
        this.microblog = microblog;
        this.author = author;
        this.items = items;
    }

    public static class Microblog {

        @Nullable
        @Json(name = "id")
        public final String id;
        @Nullable
        @Json(name = "username")
        public final String username;
        @Nullable
        @Json(name = "bio")
        public final String bio;
        @Nullable
        @Json(name = "is_following")
        public final Boolean is_following;
        @Nullable
        @Json(name = "is_you")
        public final Boolean is_you;
        @Nullable
        @Json(name = "following_count")
        public final Integer following_count;

        public Microblog(@Nullable String id, @Nullable String username, @Nullable String bio,
                         @Nullable Boolean is_following, @Nullable Boolean is_you, @Nullable Integer following_count) {
            this.id = id;
            this.username = username;
            this.bio = bio;
            this.is_following = is_following;
            this.is_you = is_you;
            this.following_count = following_count;
        }
    }
}
