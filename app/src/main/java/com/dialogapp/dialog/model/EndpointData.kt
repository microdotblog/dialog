package com.dialogapp.dialog.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import com.dialogapp.dialog.vo.MicroBlogResponse

@Entity(tableName = "endpointData", primaryKeys = ["endpoint"], indices = [Index(value = ["endpoint"], unique = true)])
data class EndpointData(val endpoint: String,
                        @Embedded(prefix = "microblog_")
                        val microblog: MicroBlogResponse.Microblog?,
                        @Embedded(prefix = "author_")
                        val author: Author?) {

    data class Microblog(val id: String?,
                         val username: String?,
                         val bio: String?,
                         val is_following: Boolean?,
                         val is_you: Boolean?,
                         val following_count: Int,
                         val discover_count: Int)
}