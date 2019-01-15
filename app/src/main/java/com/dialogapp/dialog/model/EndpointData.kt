package com.dialogapp.dialog.model

import androidx.room.Embedded
import androidx.room.Entity
import com.dialogapp.dialog.vo.Author
import com.squareup.moshi.Json

@Entity(tableName = "endpointData", primaryKeys = ["endpoint"])
data class EndpointData(val endpoint: String,
                        @Embedded(prefix = "microblog_")
                        val microblog: Microblog?,
                        @Embedded(prefix = "author_")
                        val author: Author?) {

    var lastFetched: Long = -1

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