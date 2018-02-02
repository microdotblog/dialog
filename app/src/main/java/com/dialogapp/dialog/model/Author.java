package com.dialogapp.dialog.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.squareup.moshi.Json;

@Entity(tableName = "authors", indices = {@Index(value = "author_info_username", unique = true)})
public class Author {

    // internal database column
    @PrimaryKey(autoGenerate = true)
    private long author_id;

    @Json(name = "name")
    @ColumnInfo(name = "author_name")
    public final String name;
    @Json(name = "url")
    @ColumnInfo(name = "author_url")
    public final String url;
    @Json(name = "avatar")
    @ColumnInfo(name = "author_avatar_url")
    public final String avatar;
    @Json(name = "_microblog")
    @Embedded(prefix = "author_info_")
    public final AuthorInfo microblog;

    public Author(String name, String url, String avatar, AuthorInfo microblog) {
        this.name = name;
        this.url = url;
        this.avatar = avatar;
        this.microblog = microblog;
    }

    public long getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(long author_id) {
        this.author_id = author_id;
    }

    public static class AuthorInfo {

        @Json(name = "username")
        public final String username;

        public AuthorInfo(String username) {
            this.username = username;
        }
    }
}
