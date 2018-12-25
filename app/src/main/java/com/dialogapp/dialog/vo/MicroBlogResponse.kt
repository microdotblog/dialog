package com.dialogapp.dialog.vo

import com.dialogapp.dialog.model.Author
import com.dialogapp.dialog.model.Post
import com.squareup.moshi.Json

class MicroBlogResponse(@field:Json(name = "title")
                        @Json(name = "title")
                        val title: String,
                        @field:Json(name = "home_page_url")
                        @Json(name = "home_page_url")
                        val homePageUrl: String,
                        @field:Json(name = "feed_url")
                        @Json(name = "feed_url")
                        val feedUrl: String,
                        @field:Json(name = "_microblog")
                        @Json(name = "_microblog")
                        val microblog: Microblog,
                        @field:Json(name = "author")
                        @Json(name = "author")
                        val author: Author?,
                        @field:Json(name = "items")
                        @Json(name = "items")
                        val posts: List<Post>) {

    class Microblog(@field:Json(name = "id")
                    @Json(name = "id")
                    val id: String?,
                    @field:Json(name = "username")
                    @Json(name = "username")
                    val username: String?,
                    @field:Json(name = "bio")
                    @Json(name = "bio")
                    val bio: String?,
                    @field:Json(name = "is_following")
                    @Json(name = "is_following")
                    val is_following: Boolean?,
                    @field:Json(name = "is_you")
                    @Json(name = "is_you")
                    val is_you: Boolean?,
                    @field:Json(name = "following_count")
                    @Json(name = "following_count")
                    val following_count: Int)
}
