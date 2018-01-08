package com.dialogapp.dialog.model;

import com.squareup.moshi.Json;

public class PaidSite {

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
