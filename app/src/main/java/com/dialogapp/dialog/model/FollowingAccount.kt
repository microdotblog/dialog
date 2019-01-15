package com.dialogapp.dialog.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.squareup.moshi.Json

@Entity(tableName = "following", primaryKeys = ["username", "belongsToEndpoint"], foreignKeys = [ForeignKey(
        entity = EndpointData::class,
        parentColumns = ["endpoint"],
        childColumns = ["belongsToEndpoint"])], indices = [Index(value = ["belongsToEndpoint"])]
)
data class FollowingAccount(@field:Json(name = "name")
                            @Json(name = "name")
                            val name: String,
                            @field:Json(name = "url")
                            @Json(name = "url")
                            val url: String,
                            @field:Json(name = "avatar")
                            @Json(name = "avatar")
                            val avatar: String,
                            @field:Json(name = "username")
                            @Json(name = "username")
                            val username: String,
                            @field:Json(name = "is_following")
                            @Json(name = "is_following")
                            val isFollowing: Boolean,
                            @field:Json(name = "is_you")
                            @Json(name = "is_you")
                            val isYou: Boolean) {
    var belongsToEndpoint: String = ""
}
