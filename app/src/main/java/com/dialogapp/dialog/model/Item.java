package com.dialogapp.dialog.model;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.squareup.moshi.Json;

@Entity(tableName = "posts")
public class Item {

    // Custom field
    @NonNull
    private String endpoint = "";

    @PrimaryKey
    @Json(name = "id")
    private Long id;
    @Json(name = "author")
    @Embedded(prefix = "author_")
    private Author author;
    @Json(name = "url")
    private String url;
    @Json(name = "content_html")
    private String contentHtml;
    @Json(name = "date_published")
    private String datePublished;
    @Json(name = "_microblog")
    @Embedded(prefix = "item_property_")
    private Microblog__ microblog;

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

    public Microblog__ getMicroblog() {
        return microblog;
    }

    public void setMicroblog(Microblog__ microblog) {
        this.microblog = microblog;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public static class Microblog__ {

        @Json(name = "is_deletable")
        private Boolean isDeletable;
        @Json(name = "is_favorite")
        private Boolean isFavorite;
        @Json(name = "date_relative")
        private String dateRelative;

        public Microblog__(Boolean isDeletable, Boolean isFavorite, String dateRelative) {
            this.isDeletable = isDeletable;
            this.isFavorite = isFavorite;
            this.dateRelative = dateRelative;
        }

        public Boolean getIsDeletable() {
            return isDeletable;
        }

        public void setIsDeletable(Boolean isDeletable) {
            this.isDeletable = isDeletable;
        }

        public Boolean getIsFavorite() {
            return isFavorite;
        }

        public void setIsFavorite(Boolean isFavorite) {
            this.isFavorite = isFavorite;
        }

        public String getDateRelative() {
            return dateRelative;
        }

        public void setDateRelative(String dateRelative) {
            this.dateRelative = dateRelative;
        }

    }
}