package com.dialogapp.dialog.model;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.squareup.moshi.Json;

@Entity(tableName = "posts", indices = {@Index(value = "url", unique = true), @Index(value = "post_author_id")},
        foreignKeys = @ForeignKey(entity = Author.class,
                parentColumns = "author_id", childColumns = "post_author_id",
                onUpdate = ForeignKey.CASCADE, deferred = true))
public class Item {

    // internal database columns
    @PrimaryKey(autoGenerate = true)
    public long post_id;
    public long post_author_id;

    @Json(name = "id")
    @Ignore
    private Long id;
    @Json(name = "author")
    @Ignore
    private Author author;
    @Json(name = "url")
    private String url;
    @Json(name = "content_html")
    private String contentHtml;
    @Json(name = "date_published")
    private String datePublished;
    @Json(name = "_microblog")
    @Embedded(prefix = "post_property_")
    private PostProperties microblog;

    public Item(String url, String contentHtml, String datePublished, PostProperties microblog,
                long post_author_id) {
        this.post_author_id = post_author_id;
        this.url = url;
        this.contentHtml = contentHtml;
        this.datePublished = datePublished;
        this.microblog = microblog;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContentHtml() {
        return contentHtml;
    }

    public void setContentHtml(String contentHtml) {
        this.contentHtml = contentHtml;
    }

    public String getDatePublished() {
        return datePublished;
    }

    public void setDatePublished(String datePublished) {
        this.datePublished = datePublished;
    }

    public PostProperties getMicroblog() {
        return microblog;
    }

    public void setMicroblog(PostProperties microblog) {
        this.microblog = microblog;
    }

    public static class PostProperties {

        @Json(name = "is_deletable")
        public final Boolean isDeletable;
        @Json(name = "is_favorite")
        public final Boolean isFavorite;
        @Json(name = "date_relative")
        public final String dateRelative;

        public PostProperties(Boolean isDeletable, Boolean isFavorite, String dateRelative) {
            this.isDeletable = isDeletable;
            this.isFavorite = isFavorite;
            this.dateRelative = dateRelative;
        }
    }
}