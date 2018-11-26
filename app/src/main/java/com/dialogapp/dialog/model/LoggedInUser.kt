package com.dialogapp.dialog.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(tableName = "logged_in_user")
data class LoggedInUser(
        @ColumnInfo(name = "token")
        @Json(name = "token")
        val token: String,

        @ColumnInfo(name = "full_name")
        @Json(name = "full_name")
        val fullName: String,

        @PrimaryKey
        @ColumnInfo(name = "username")
        @Json(name = "username")
        val username: String,

        @ColumnInfo(name = "gravatar_url")
        @Json(name = "gravatar_url")
        val gravatarUrl: String,

        @ColumnInfo(name = "has_site")
        @Json(name = "has_site")
        val hasSite: Boolean,

        @ColumnInfo(name = "is_fullaccess")
        @Json(name = "is_fullaccess")
        val isFullaccess: Boolean,

        @ColumnInfo(name = "default_site")
        @Json(name = "default_site")
        val defaultSite: String
)