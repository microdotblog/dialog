package com.dialogapp.dialog.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.squareup.moshi.Json

@Entity(tableName = "posts", primaryKeys = ["id", "belongsToEndpoint"], foreignKeys = [ForeignKey(
        entity = EndpointData::class,
        parentColumns = ["endpoint"],
        childColumns = ["belongsToEndpoint"])], indices = [Index(value = ["belongsToEndpoint"], unique = false)]
)
data class Post(@field:Json(name = "id")
                @Json(name = "id")
                val id: String,
                @field:Json(name = "content_html")
                @Json(name = "content_html")
                val contentHtml: String,
                @field:Json(name = "url")
                @Json(name = "url")
                val url: String,
                @field:Json(name = "date_published")
                @Json(name = "date_published")
                val datePublished: String,
                @field:Json(name = "author")
                @Json(name = "author")
                @Embedded(prefix = "author_")
                val author: Author,
                @field:Json(name = "_microblog")
                @Json(name = "_microblog")
                @Embedded(prefix = "post_property_")
                val microblog: PostProperties) {

    var belongsToEndpoint: String = ""

    data class PostProperties(@field:Json(name = "date_relative")
                              @Json(name = "date_relative")
                              val dateRelative: String,
                              @field:Json(name = "is_deletable")
                              @Json(name = "is_deletable")
                              val isDeletable: Boolean,
                              @field:Json(name = "is_favorite")
                              @Json(name = "is_favorite")
                              val isFavorite: Boolean,
                              @field:Json(name = "is_conversation")
                              @Json(name = "is_conversation")
                              val isConversation: Boolean)
}