package com.dialogapp.dialog.model

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(tableName = "account")
data class AccountResponse(@field:Json(name = "full_name") var fullName: String?,
                           @field:Json(name = "email") var email: String?,
                           @param:NonNull
                           @field:PrimaryKey
                           @field:NonNull
                           @field:Json(name = "username")
                           @get:NonNull
                           var username: String?,
                           @field:Json(name = "web_site") var webSite: String?,
                           @field:Json(name = "about_me") var aboutMe: String?)
