package com.dialogapp.dialog.api;

import android.arch.lifecycle.LiveData;

import com.dialogapp.dialog.model.AccountResponse;
import com.dialogapp.dialog.model.MediaEndPoint;
import com.dialogapp.dialog.model.MicroBlogResponse;
import com.dialogapp.dialog.model.VerifiedAccount;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * REST API access points
 */
public interface MicroblogService {

    // GET

    @GET("account/info")
    LiveData<ApiResponse<AccountResponse>> getAccountData();

    @GET("posts/all")
    LiveData<ApiResponse<MicroBlogResponse>> getTimeLine(@Query("since_id") String id);

    @GET("posts/mentions")
    LiveData<ApiResponse<MicroBlogResponse>> getMentions(@Query("since_id") String id);

    @GET("posts/favorites")
    LiveData<ApiResponse<MicroBlogResponse>> getFavorites();

    @GET("posts/discover")
    Call<MicroBlogResponse> getFeaturedPosts();

    @GET("posts/{username}")
    LiveData<ApiResponse<MicroBlogResponse>> getPostsByUsername(@Path("username") String username);

    @GET("posts/conversation")
    Call<MicroBlogResponse> getConversation(@Query("id") long id);

    @GET("posts/check")
    Call<MicroBlogResponse> getNewPostsSince(@Query("since_id") long since_id);

    // POST

    @FormUrlEncoded
    @Headers("NO-AUTH: true")
    @POST("account/verify")
    LiveData<ApiResponse<VerifiedAccount>> verifyToken(@Field("token") String token);

    @FormUrlEncoded
    @POST("posts/favorites")
    Call<Response> favoriteAPost(@Field("id") String id);

    @FormUrlEncoded
    @POST("posts/reply")
    Call<Response> replyToPost(@Field("id") String id);

    @FormUrlEncoded
    @POST("users/follow")
    Call<Response> followUser(@Field("username") String user);

    @FormUrlEncoded
    @POST("users/unfollow")
    Call<Response> unfollowUser(@Field("username") String user);

    // DELETE

    @DELETE("posts/favorites/{id}")
    Call<Response> unfavoriteAPost(@Path("id") String id);

    @DELETE("posts/{id}")
    Call<Response> deleteById(@Path("id") String id);

    // Micropub Posting API

    @GET("micropub?q=config")
    Call<MediaEndPoint> getMediaEndpoint();

    @FormUrlEncoded
    @POST("micropub")
    Call<Response> createNewPost(@Field("h") String type,
                                 @Field("content") String content);

    @FormUrlEncoded
    @POST("micropub")
    Call<Response> createNewPostWithTitle(@Field("h") String type,
                                          @Field("name") String title,
                                          @Field("content") String content);
}
