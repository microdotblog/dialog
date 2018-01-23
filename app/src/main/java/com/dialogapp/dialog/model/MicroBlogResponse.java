package com.dialogapp.dialog.model;

import com.squareup.moshi.Json;

import java.util.List;

public class MicroBlogResponse {

    @Json(name = "title")
    public final String title;
    @Json(name = "home_page_url")
    public final String homePageUrl;
    @Json(name = "feed_url")
    public final String feedUrl;
    @Json(name = "_microblog")
    public final Microblog microblog;
    @Json(name = "items")
    public final List<Item> items;

    public MicroBlogResponse(String title, String homePageUrl, String feedUrl, Microblog microblog,
                             List<Item> items) {
        this.title = title;
        this.homePageUrl = homePageUrl;
        this.feedUrl = feedUrl;
        this.microblog = microblog;
        this.items = items;
    }

    public static class Microblog {

        @Json(name = "about")
        public final String about;

        public Microblog(String about) {
            this.about = about;
        }
    }
}
