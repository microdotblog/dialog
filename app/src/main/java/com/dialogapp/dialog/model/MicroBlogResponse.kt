package com.dialogapp.dialog.model

import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import com.squareup.moshi.Json

@Entity(tableName = "microblogData", primaryKeys = ["feedUrl"])
data class MicroBlogResponse(@field:Json(name = "title")
                             val title: String,
                             @field:Json(name = "home_page_url")
                             val homePageUrl: String,
                             @field:NonNull
                             @field:Json(name = "feed_url")
                             val feedUrl: String,
                             @field:Json(name = "_microblog")
                             @field:Embedded(prefix = "microblog_")
                             val microblog: Microblog,
                             @param:Nullable
                             @field:Nullable
                             @field:Json(name = "author")
                             @field:Embedded(prefix = "author_")
                             val author: Author,
                             @field:Json(name = "items")
                             val posts: List<Post>) {

    data class Microblog(@param:Nullable
                         @field:Nullable
                         @field:Json(name = "id")
                         val id: String,
                         @param:Nullable
                         @field:Nullable
                         @field:Json(name = "username")
                         val username: String,
                         @param:Nullable
                         @field:Nullable
                         @field:Json(name = "bio")
                         val bio: String,
                         @param:Nullable
                         @field:Nullable
                         @field:Json(name = "is_following")
                         val is_following: Boolean?,
                         @param:Nullable
                         @field:Nullable
                         @field:Json(name = "is_you")
                         val is_you: Boolean?,
                         @param:Nullable
                         @field:Nullable
                         @field:Json(name = "following_count")
                         val following_count: Int?)
}
