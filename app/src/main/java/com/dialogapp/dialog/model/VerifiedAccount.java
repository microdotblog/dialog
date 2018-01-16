package com.dialogapp.dialog.model;

import android.support.annotation.Nullable;

import com.squareup.moshi.Json;

public class VerifiedAccount {
    // all fields except error will be null if account is invalid
    @Nullable
    @Json(name = "full_name")
    public final String fullName;

    @Nullable
    @Json(name = "username")
    public final String username;

    @Nullable
    @Json(name = "gravatar_url")
    public final String gravatarUrl;

    @Nullable
    @Json(name = "has_site")
    public final Boolean hasSite;

    @Nullable
    @Json(name = "is_fullaccess")
    public final Boolean isFullaccess;

    @Nullable
    @Json(name = "default_site")
    public final String defaultSite;

    // null if account is valid
    @Nullable
    @Json(name = "error")
    public final String error;

    public VerifiedAccount(@Nullable String fullName, @Nullable String username, @Nullable String gravatarUrl,
                           @Nullable Boolean hasSite, @Nullable Boolean isFullaccess,
                           @Nullable String defaultSite, @Nullable String error) {
        this.fullName = fullName;
        this.username = username;
        this.gravatarUrl = gravatarUrl;
        this.hasSite = hasSite;
        this.isFullaccess = isFullaccess;
        this.defaultSite = defaultSite;
        this.error = error;
    }
}
