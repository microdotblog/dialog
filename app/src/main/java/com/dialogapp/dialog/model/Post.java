package com.dialogapp.dialog.model;

/**
 * Custom Post model used by the views
 */

public class Post {

    public final long id;
    public final String url;
    public final String contentHtml;
    public final String datePublished;

    public final String post_property_dateRelative;
    public final Boolean post_property_isDeletable;
    public final Boolean post_property_isFavorite;

    public final String author_name;
    public final String author_url;
    public final String author_avatar_url;
    public final String author_info_username;

    public Post(long id, String url, String contentHtml, String datePublished,
                String post_property_dateRelative, Boolean post_property_isDeletable,
                Boolean post_property_isFavorite,
                String author_name, String author_url, String author_avatar_url, String author_info_username) {
        this.id = id;
        this.url = url;
        this.contentHtml = contentHtml;
        this.datePublished = datePublished;
        this.post_property_dateRelative = post_property_dateRelative;
        this.post_property_isDeletable = post_property_isDeletable;
        this.post_property_isFavorite = post_property_isFavorite;
        this.author_name = author_name;
        this.author_url = author_url;
        this.author_avatar_url = author_avatar_url;
        this.author_info_username = author_info_username;
    }
}
