package com.dialogapp.dialog.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.annotation.NonNull

import com.squareup.moshi.Json

@Entity(tableName = "posts", primaryKeys = ["id", "endpoint"])
data class Post(@param:NonNull
           @field:NonNull
           @field:Json(name = "id")
           val id: String,
           @field:Json(name = "content_html")
           val contentHtml: String,
           @field:Json(name = "url")
           val url: String,
           @field:Json(name = "date_published")
           val datePublished: String,
           @field:Json(name = "author")
           @field:Embedded(prefix = "author_")
           val author: Author,
           @field:Json(name = "_microblog")
           @field:Embedded(prefix = "post_property_")
           val microblog: PostProperties,
           @param:NonNull
           @field:NonNull
           @get:NonNull
           var endpoint: String?) {

    data class PostProperties(@field:Json(name = "date_relative")
                         val dateRelative: String,
                         @field:Json(name = "is_deletable")
                         val isDeletable: Boolean?,
                         @field:Json(name = "is_favorite")
                         val isFavorite: Boolean?,
                         @field:Json(name = "is_conversation")
                         val isConversation: Boolean?)
}