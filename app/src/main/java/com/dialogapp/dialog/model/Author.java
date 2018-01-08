package com.dialogapp.dialog.model;

import com.squareup.moshi.Json;

public class Author {

    @Json(name = "name")
    private String name;
    @Json(name = "url")
    private String url;
    @Json(name = "avatar")
    private String avatar;
    @Json(name = "_microblog")
    private Microblog_ microblog;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Microblog_ getMicroblog() {
        return microblog;
    }

    public void setMicroblog(Microblog_ microblog) {
        this.microblog = microblog;
    }

    public static class Microblog_ {

        @Json(name = "username")
        private String username;
        @Json(name = "is_following")
        private Boolean isFollowing;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public Boolean getIsFollowing() {
            return isFollowing;
        }

        public void setIsFollowing(Boolean isFollowing) {
            this.isFollowing = isFollowing;
        }

    }
}