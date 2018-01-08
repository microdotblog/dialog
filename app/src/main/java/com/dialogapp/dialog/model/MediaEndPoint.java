package com.dialogapp.dialog.model;

import com.squareup.moshi.Json;

public class MediaEndPoint {

    @Json(name = "media-endpoint")
    private String mediaEndpoint;

    public String getMediaEndpoint() {
        return mediaEndpoint;
    }

    public void setMediaEndpoint(String mediaEndpoint) {
        this.mediaEndpoint = mediaEndpoint;
    }
}
