package com.dialogapp.dialog.vo

import com.squareup.moshi.Json

data class LoggedInUser(
        @Json(name = "token")
        val token: String,

        @Json(name = "full_name")
        val fullName: String,

        @Json(name = "username")
        val username: String,

        @Json(name = "gravatar_url")
        val gravatarUrl: String,

        @Json(name = "has_site")
        val hasSite: Boolean,

        @Json(name = "is_fullaccess")
        val isFullaccess: Boolean,

        @Json(name = "default_site")
        val defaultSite: String
)