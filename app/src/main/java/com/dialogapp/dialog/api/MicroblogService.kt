package com.dialogapp.dialog.api

import com.dialogapp.dialog.vo.MicroBlogResponse
import com.dialogapp.dialog.vo.VerifiedAccount
import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

/**
 * REST API access points
 */
interface MicroblogService {

    // GET

    @GET("posts/{endpoint}")
    fun getEndpoint(@Path("endpoint") endpoint: String,
                    @Query("before_id") beforeId: String?,
                    @Query("count") count: Int): Call<MicroBlogResponse>

    // POST

    @FormUrlEncoded
    @Headers("NO-AUTH: true")
    @POST("account/verify")
    fun verifyToken(@Field("token") token: String): Deferred<Response<VerifiedAccount>>
}
