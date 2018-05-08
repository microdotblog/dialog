package com.dialogapp.dialog.model;

import com.squareup.moshi.Json;

public class AccountInfo {
    @Json(name = "name")
    public final String name;
    @Json(name = "url")
    public final String url;
    @Json(name = "avatar")
    public final String avatar;
    @Json(name = "username")
    public final String username;
    @Json(name = "is_following")
    public final boolean isFollowing;
    @Json(name = "is_you")
    public final boolean isYou;

    public AccountInfo(String name, String url, String avatar, String username, boolean isFollowing, boolean isYou) {
        this.name = name;
        this.url = url;
        this.avatar = avatar;
        this.username = username;
        this.isFollowing = isFollowing;
        this.isYou = isYou;
    }
}
