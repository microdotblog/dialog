package com.dialogapp.dialog.auth

import com.dialogapp.dialog.api.MicroblogService
import com.dialogapp.dialog.model.LoggedInUser
import com.dialogapp.dialog.util.safeApiCall
import com.dialogapp.dialog.vo.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlainTokenAuthenticator @Inject constructor(private val microblogService: MicroblogService) {

    suspend fun verify(inputToken: String) = safeApiCall(
            call = { requestVerification(inputToken) },
            errorMessage = "Error verifying token"
    )

    private suspend fun requestVerification(inputToken: String): Resource<LoggedInUser> {
        val response = microblogService.verifyToken(inputToken).await()

        if (response.isSuccessful) {
            val verifiedAccount = response.body()
            if (verifiedAccount != null) {
                return if (verifiedAccount.error == null) {
                    val user = LoggedInUser(verifiedAccount.token!!, verifiedAccount.fullName!!,
                            verifiedAccount.username!!, verifiedAccount.gravatarUrl!!,
                            verifiedAccount.hasSite!!, verifiedAccount.isFullaccess!!, verifiedAccount.defaultSite!!)
                    Resource.success(user)
                } else {
                    Resource.error("Invalid token", null)
                }
            }
        }
        return Resource.error("Could not verify token ${response.code()} ${response.message()}", null)
    }
}
