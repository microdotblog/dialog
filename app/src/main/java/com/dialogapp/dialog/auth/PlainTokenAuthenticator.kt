package com.dialogapp.dialog.auth

import com.dialogapp.dialog.api.ApiEmptyResponse
import com.dialogapp.dialog.api.ApiErrorResponse
import com.dialogapp.dialog.api.ApiSuccessResponse
import com.dialogapp.dialog.api.MicroblogService
import com.dialogapp.dialog.model.LoggedInUser
import com.dialogapp.dialog.vo.Resource
import javax.inject.Inject

class PlainTokenAuthenticator @Inject constructor(private val microblogService: MicroblogService) {

    suspend fun authenticate(inputToken: String): Resource<LoggedInUser> {
        val response = microblogService.verifyToken(inputToken).await()

        when (response) {
            is ApiSuccessResponse -> {
                val verifiedAccount = response.body
                return if (verifiedAccount.error == null) {
                    val user = LoggedInUser(verifiedAccount.token!!, verifiedAccount.fullName!!,
                            verifiedAccount.username!!, verifiedAccount.gravatarUrl!!,
                            verifiedAccount.hasSite!!, verifiedAccount.isFullaccess!!, verifiedAccount.defaultSite!!)
                    Resource.success(user)
                } else {
                    Resource.error("Invalid token", null)
                }
            }
            is ApiEmptyResponse -> {
                return Resource.error("Empty response", null)
            }
            is ApiErrorResponse -> {
                return Resource.error("Error response", null)
            }
        }
    }
}
