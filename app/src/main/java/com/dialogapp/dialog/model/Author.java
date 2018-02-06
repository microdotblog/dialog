package com.dialogapp.dialog.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;

import com.squareup.moshi.Json;

public class Author {
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

    public static class AuthorInfo {

        @Json(name = "username")
        public final String username;

        public AuthorInfo(String username) {
            this.username = username;
        }
    }
}
