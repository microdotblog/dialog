package com.dialogapp.dialog.model;

import com.squareup.moshi.Json;

public class RssFeed {

    @Json(name = "id")
    private Long id;
    @Json(name = "url")
    private String url;

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
