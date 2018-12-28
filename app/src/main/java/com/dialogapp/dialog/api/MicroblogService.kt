package com.dialogapp.dialog.api

import androidx.lifecycle.LiveData
import com.dialogapp.dialog.model.FollowingAccount
import com.dialogapp.dialog.util.calladapters.ApiResponseCall
import com.dialogapp.dialog.vo.MicroBlogResponse
import com.dialogapp.dialog.vo.VerifiedAccount
import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
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
                    @Query("count") count: Int?): ApiResponseCall<MicroBlogResponse>

    @GET("posts/discover")
    fun getFeaturedPosts(): LiveData<ApiResponse<MicroBlogResponse>>

    @GET("posts/discover/{topic}")
    fun getFeaturedPosts(@Path("topic") topic: String): LiveData<ApiResponse<MicroBlogResponse>>

    @GET("posts/{username}")
    fun getPostsByUsername(@Path("username") username: String): LiveData<ApiResponse<MicroBlogResponse>>

    @GET("posts/conversation")
    fun getConversation(@Query("id") id: String): LiveData<ApiResponse<MicroBlogResponse>>

    @GET("posts/favorites")
    fun getFavorites(): LiveData<ApiResponse<MicroBlogResponse>>

    @GET("users/following/{username}")
    fun getFollowingSelf(@Path("username") username: String): LiveData<ApiResponse<List<FollowingAccount>>>

    @GET("users/discover/{username}")
    fun getFollowing(@Path("username") username: String): LiveData<ApiResponse<List<FollowingAccount>>>

    // POST

    @FormUrlEncoded
    @Headers("NO-AUTH: true")
    @POST("account/verify")
    fun verifyToken(@Field("token") token: String): Deferred<Response<VerifiedAccount>>

    @FormUrlEncoded
    @POST("posts/favorites")
    fun favoritePost(@Field("id") id: String): Deferred<Response<ResponseBody>>

    @FormUrlEncoded
    @POST("users/follow")
    fun followUser(@Field("username") user: String): Deferred<Response<ResponseBody>>

    @FormUrlEncoded
    @POST("users/unfollow")
    fun unfollowUser(@Field("username") user: String): Deferred<Response<ResponseBody>>

    @FormUrlEncoded
    @POST("posts/reply")
    fun replyToPost(@Field("id") id: String, @Field("text") content: String)
            : Deferred<Response<ResponseBody>>

    // DELETE

    @DELETE("posts/favorites/{id}")
    fun unfavoritePost(@Path("id") id: String): Deferred<Response<ResponseBody>>

    @DELETE("posts/{id}")
    fun deletePost(@Path("id") id: String): Deferred<Response<ResponseBody>>
}
