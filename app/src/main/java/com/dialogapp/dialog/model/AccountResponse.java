package com.dialogapp.dialog.model;

import com.squareup.moshi.Json;

import java.util.List;

public class AccountResponse {

    @Json(name = "full_name")
    private String fullName;
    @Json(name = "email")
    private String email;
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
    @Json(name = "paid_bots")
    private List<Object> paidBots = null;
    @Json(name = "has_site")
    private Boolean hasSite;
    @Json(name = "has_bot")
    private Boolean hasBot;

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
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

    public List<Object> getPaidBots() {
        return paidBots;
    }

    public void setPaidBots(List<Object> paidBots) {
        this.paidBots = paidBots;
    }

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

}
