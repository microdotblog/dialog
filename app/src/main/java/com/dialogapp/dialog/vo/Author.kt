package com.dialogapp.dialog.vo

import androidx.room.Embedded
import com.squareup.moshi.Json

data class Author(@field:Json(name = "name")
                  @Json(name = "name")
                  val name: String,
                  @field:Json(name = "url")
                  @Json(name = "url")
                  val url: String,
                  @field:Json(name = "avatar")
                  @Json(name = "avatar")
                  val avatar: String,
                  @field:Json(name = "_microblog")
                  @Json(name = "_microblog")
                  @field:Embedded(prefix = "author_info_")
                  val microblog: AuthorInfo?) {

    class AuthorInfo(@field:Json(name = "username")
                          @Json(name = "username")
                          val username: String)
}
