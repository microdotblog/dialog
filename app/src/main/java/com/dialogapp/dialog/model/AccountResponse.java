package com.dialogapp.dialog.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.dialogapp.dialog.db.ListTypeConverters;
import com.squareup.moshi.Json;

import java.util.List;

@Entity(tableName = "account")
@TypeConverters(ListTypeConverters.class)
public class AccountResponse {

    @Json(name = "full_name")
    private String fullName;
    @Json(name = "email")
    private String email;
    @PrimaryKey
    @NonNull
    @Json(name = "username")
    private String username;
    @Json(name = "web_site")
    private String webSite;
    @Json(name = "about_me")
    private String aboutMe;
    @Json(name = "rss_feeds")
    private List<RssFeed> rssFeeds = null;
    @Json(name = "paid_sites")
    private List<PaidSite> paidSites = null;

//    @Json(name = "paid_bots")
//    private List<Object> paidBots = null;

    @Json(name = "has_site")
    private Boolean hasSite;
    @Json(name = "has_bot")
    private Boolean hasBot;

    public AccountResponse(String fullName, String email, @NonNull String username, String webSite,
                           String aboutMe, List<RssFeed> rssFeeds, List<PaidSite> paidSites,
                           Boolean hasSite, Boolean hasBot) {
        this.fullName = fullName;
        this.email = email;
        this.username = username;
        this.webSite = webSite;
        this.aboutMe = aboutMe;
        this.rssFeeds = rssFeeds;
        this.paidSites = paidSites;
        this.hasSite = hasSite;
        this.hasBot = hasBot;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @NonNull
    public String getUsername() {
        return username;
    }

    public void setUsername(@NonNull String username) {
        this.username = username;
    }

    public String getWebSite() {
        return webSite;
    }

    public void setWebSite(String webSite) {
        this.webSite = webSite;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public List<RssFeed> getRssFeeds() {
        return rssFeeds;
    }

    public void setRssFeeds(List<RssFeed> rssFeeds) {
        this.rssFeeds = rssFeeds;
    }

    public List<PaidSite> getPaidSites() {
        return paidSites;
    }

    public void setPaidSites(List<PaidSite> paidSites) {
        this.paidSites = paidSites;
    }

//    public List<Object> getPaidBots() {
//        return paidBots;
//    }
//
//    public void setPaidBots(List<Object> paidBots) {
//        this.paidBots = paidBots;
//    }

    public Boolean getHasSite() {
        return hasSite;
    }

    public void setHasSite(Boolean hasSite) {
        this.hasSite = hasSite;
    }

    public Boolean getHasBot() {
        return hasBot;
    }

    public void setHasBot(Boolean hasBot) {
        this.hasBot = hasBot;
    }


    public static class RssFeed {

        @Json(name = "id")
        private Long id;
        @Json(name = "url")
        private String url;

        public RssFeed(Long id, String url) {
            this.id = id;
            this.url = url;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

    }

    public static class PaidSite {

        @Json(name = "id")
        private Long id;
        @Json(name = "guid")
        private String guid;
        @Json(name = "hostname")
        private String hostname;
        @Json(name = "url")
        private String url;
        @Json(name = "created_at")
        private String createdAt;
        @Json(name = "posts_summary")
        private String postsSummary;

        public PaidSite(Long id, String guid, String hostname, String url, String createdAt, String postsSummary) {
            this.id = id;
            this.guid = guid;
            this.hostname = hostname;
            this.url = url;
            this.createdAt = createdAt;
            this.postsSummary = postsSummary;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getGuid() {
            return guid;
        }

        public void setGuid(String guid) {
            this.guid = guid;
        }

        public String getHostname() {
            return hostname;
        }

        public void setHostname(String hostname) {
            this.hostname = hostname;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getPostsSummary() {
            return postsSummary;
        }

        public void setPostsSummary(String postsSummary) {
            this.postsSummary = postsSummary;
        }

    }

}
