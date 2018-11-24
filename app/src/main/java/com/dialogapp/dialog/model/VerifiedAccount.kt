package com.dialogapp.dialog.model

import androidx.annotation.Nullable

import com.squareup.moshi.Json

// all fields except error will be null if account is invalid
class VerifiedAccount(
        @param:Nullable @field:Nullable
        @field:Json(name = "full_name")
        val fullName: String, @param:Nullable @field:Nullable
        @field:Json(name = "username")
        val username: String, @param:Nullable @field:Nullable
        @field:Json(name = "gravatar_url")
        val gravatarUrl: String,
        @param:Nullable @field:Nullable
        @field:Json(name = "has_site")
        val hasSite: Boolean?, @param:Nullable @field:Nullable
        @field:Json(name = "is_fullaccess")
        val isFullaccess: Boolean?,
        @param:Nullable @field:Nullable
        @field:Json(name = "default_site")
        val defaultSite: String,

        // null if account is valid
        @param:Nullable @field:Nullable
        @field:Json(name = "error")
        val error: String)
