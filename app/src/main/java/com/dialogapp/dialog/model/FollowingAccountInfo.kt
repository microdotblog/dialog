package com.dialogapp.dialog.model

import com.squareup.moshi.Json

class FollowingAccountInfo(@field:Json(name = "name")
                           val name: String, @field:Json(name = "url")
                           val url: String, @field:Json(name = "avatar")
                           val avatar: String, @field:Json(name = "username")
                           val username: String,
                           @field:Json(name = "is_following")
                           val isFollowing: Boolean,
                           @field:Json(name = "is_you")
                           val isYou: Boolean)
