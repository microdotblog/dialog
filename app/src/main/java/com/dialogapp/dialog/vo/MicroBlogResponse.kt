package com.dialogapp.dialog.vo

import com.dialogapp.dialog.model.EndpointData
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
                        val microblog: EndpointData.Microblog?,
                        @field:Json(name = "author")
                        @Json(name = "author")
                        val author: Author?,
                        @field:Json(name = "items")
                        @Json(name = "items")
                        val posts: List<Post>)
