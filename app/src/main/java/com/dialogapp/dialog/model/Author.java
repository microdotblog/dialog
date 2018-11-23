package com.dialogapp.dialog.model;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.annotation.Nullable;

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
    @Nullable
    @Json(name = "_microblog")
    @Embedded(prefix = "author_info_")
    public final AuthorInfo microblog;

    public Author(String name, String url, String avatar, @Nullable AuthorInfo microblog) {
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
