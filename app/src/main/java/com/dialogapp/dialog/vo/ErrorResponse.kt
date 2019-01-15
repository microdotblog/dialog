package com.dialogapp.dialog.vo

import com.squareup.moshi.Json

class ErrorResponse(
        @field:Json(name = "error")
        val error: String?,
        @field:Json(name = "error_description")
        val error_description: String?
)