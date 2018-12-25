package com.dialogapp.dialog.vo

import androidx.annotation.Nullable

import com.squareup.moshi.Json

// all fields except error will be null if account is invalid
class VerifiedAccount(
        @Nullable
        @field:Json(name = "token")
        val token: String?,
        @Nullable
        @field:Json(name = "full_name")
        val fullName: String?,
        @Nullable
        @field:Json(name = "username")
        val username: String?,
        @Nullable
        @field:Json(name = "gravatar_url")
        val gravatarUrl: String?,
        @Nullable
        @field:Json(name = "has_site")
        val hasSite: Boolean?,
        @Nullable
        @field:Json(name = "is_fullaccess")
        val isFullaccess: Boolean?,
        @Nullable
        @field:Json(name = "default_site")
        val defaultSite: String?,

        // null if account is valid
        @Nullable
        @field:Json(name = "error")
        val error: String?)
