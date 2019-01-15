package com.dialogapp.dialog.auth

import com.dialogapp.dialog.api.MicroblogService
import com.dialogapp.dialog.loggedInUser
import com.dialogapp.dialog.vo.VerifiedAccount
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import retrofit2.Response

class PlainTokenAuthenticatorTest {

    private val successResponse = VerifiedAccount("abc", "fullname", "username",
            "avatar", true, false, "default", null)
    private val failureResponse = VerifiedAccount(null, null, null,
            null, null, null, null, "Invalid API token")

    private val microblogService: MicroblogService = mock()
    private val plainTokenAuthenticator = PlainTokenAuthenticator(microblogService)

    @Test
    fun verify_SuccessfulValidToken_LoggedInUser() = runBlocking {
        whenever(microblogService.verifyToken("abc")).thenReturn(CompletableDeferred(Response.success(successResponse)))
        val result = plainTokenAuthenticator.verify("abc")
        assertEquals(result.data, loggedInUser)
    }

    @Test
    fun verify_SuccessfulInvalidToken_Null() = runBlocking {
        whenever(microblogService.verifyToken("abc")).thenReturn(CompletableDeferred(Response.success(failureResponse)))
        val result = plainTokenAuthenticator.verify("abc")
        assertNull(result.data)
        assertEquals(result.message, "Invalid token")
    }

    @Test
    fun verify_Unsuccessful_Error() = runBlocking {
        whenever(microblogService.verifyToken("abc")).thenReturn(CompletableDeferred(Response.error(400,
                ResponseBody.create(MediaType.parse(""), "Error"))))
        val result = plainTokenAuthenticator.verify("abc")
        assertNull(result.data)
        assertEquals(result.message, "Could not verify token 400 Response.error()")
    }
}