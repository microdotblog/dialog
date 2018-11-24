package com.dialogapp.dialog.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.annotation.Nullable

import com.squareup.moshi.Json

data class Author(@field:Json(name = "name")
                  @field:ColumnInfo(name = "author_name")
                  val name: String,
                  @field:Json(name = "url")
                  @field:ColumnInfo(name = "author_url")
                  val url: String,
                  @field:Json(name = "avatar")
                  @field:ColumnInfo(name = "author_avatar_url")
                  val avatar: String,
                  @param:Nullable
                  @field:Nullable
                  @field:Json(name = "_microblog")
                  @field:Embedded(prefix = "author_info_")
                  val microblog: AuthorInfo) {

    data class AuthorInfo(@field:Json(name = "username")
                          val username: String)
}
