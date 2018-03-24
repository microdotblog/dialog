package com.dialogapp.dialog.model;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;

import com.squareup.moshi.Json;

@Entity(tableName = "posts", primaryKeys = {"id", "endpoint"})
public class Item {
    // Custom field
    @NonNull
    private String endpoint;

    @NonNull
    @Json(name = "id")
    public final Long id;
    @Json(name = "content_html")
    public final String contentHtml;
    @Json(name = "url")
    public final String url;
    @Json(name = "date_published")
    public final String datePublished;
    @Json(name = "author")
    @Embedded(prefix = "author_")
    public final Author author;
    @Json(name = "_microblog")
    @Embedded(prefix = "post_property_")
    public final PostProperties microblog;

    public Item(@NonNull String endpoint, @NonNull Long id, String contentHtml, String url,
                String datePublished, Author author, PostProperties microblog) {
        this.endpoint = endpoint;
        this.id = id;
        this.contentHtml = contentHtml;
        this.url = url;
        this.datePublished = datePublished;
        this.author = author;
        this.microblog = microblog;
    }

    @NonNull
    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(@NonNull String endpoint) {
        this.endpoint = endpoint;
    }

    public static class PostProperties {

        @Json(name = "date_relative")
        public final String dateRelative;
        @Json(name = "is_deletable")
        public final Boolean isDeletable;
        @Json(name = "is_favorite")
        public final Boolean isFavorite;
        @Json(name = "is_conversation")
        public final Boolean isConversation;

        public PostProperties(String dateRelative, Boolean isDeletable, Boolean isFavorite, Boolean isConversation) {
            this.dateRelative = dateRelative;
            this.isDeletable = isDeletable;
            this.isFavorite = isFavorite;
            this.isConversation = isConversation;
        }
    }
}