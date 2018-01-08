package com.dialogapp.dialog.model;

import com.squareup.moshi.Json;

import java.util.List;

public class MicroBlogResponse {

    @Json(name = "title")
    private String title;
    @Json(name = "home_page_url")
    private String homePageUrl;
    @Json(name = "feed_url")
    private String feedUrl;
    @Json(name = "_microblog")
    private Microblog microblog;
    @Json(name = "items")
    private List<Item> items = null;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHomePageUrl() {
        return homePageUrl;
    }

    public void setHomePageUrl(String homePageUrl) {
        this.homePageUrl = homePageUrl;
    }

    public String getFeedUrl() {
        return feedUrl;
    }

    public void setFeedUrl(String feedUrl) {
        this.feedUrl = feedUrl;
    }

    public Microblog getMicroblog() {
        return microblog;
    }

    public void setMicroblog(Microblog microblog) {
        this.microblog = microblog;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public static class Microblog {

        @Json(name = "about")
        private String about;

        public String getAbout() {
            return about;
        }

        public void setAbout(String about) {
            this.about = about;
        }

    }
}
