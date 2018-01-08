package com.dialogapp.dialog.api;

import com.dialogapp.dialog.model.AccountResponse;
import com.dialogapp.dialog.model.MediaEndPoint;
import com.dialogapp.dialog.model.MicroBlogResponse;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * REST API access points
 */
public interface MicroblogService {

    // GET

    @GET("account/info")
    Call<AccountResponse> getAccountData(@Header("Authorization") String token);

    @GET("posts/all")
    Call<MicroBlogResponse> getTimeLine(@Header("Authorization") String token);

    @GET("posts/mentions")
    Call<MicroBlogResponse> getMentions(@Header("Authorization") String token);

    @GET("posts/favorites")
    Call<MicroBlogResponse> getFavorites(@Header("Authorization") String token);

    @GET("posts/discover")
    Call<MicroBlogResponse> getFeaturedPosts(@Header("Authorization") String token);

    @GET("posts/{username}")
    Call<MicroBlogResponse> getPostsByUsername(@Header("Authorization") String token,
                                               @Path("username") String username);

    @GET("posts/conversation")
    Call<MicroBlogResponse> getConversation(@Header("Authorization") String token,
                                            @Query("id") long id);

    @GET("posts/check")
    Call<MicroBlogResponse> getNewPostsSince(@Header("Authorization") String token,
                                             @Query("since_id") long since_id);

    // POST

    @FormUrlEncoded
    @POST("posts/favorites")
    Call<Response> favoriteAPost(@Header("Authorization") String token,
                                 @Field("id") String id);

    @FormUrlEncoded
    @POST("posts/reply")
    Call<Response> replyToPost(@Header("Authorization") String token,
                               @Field("id") String id);

    @FormUrlEncoded
    @POST("users/follow")
    Call<Response> followUser(@Header("Authorization") String token,
                              @Field("username") String user);

    @FormUrlEncoded
    @POST("users/unfollow")
    Call<Response> unfollowUser(@Header("Authorization") String token,
                                @Field("username") String user);

    // DELETE

    @DELETE("posts/favorites/{id}")
    Call<Response> unfavoriteAPost(@Header("Authorization") String token, @Path("id") String id);

    @DELETE("posts/{id}")
    Call<Response> deleteById(@Header("Authorization") String token, @Path("id") String id);

    // Micropub Posting API

    @GET("micropub?q=config")
    Call<MediaEndPoint> getMediaEndpoint(@Header("Authorization") String token);

    @FormUrlEncoded
    @POST("micropub")
    Call<Response> createNewPost(@Header("Authorization") String token,
                                 @Field("h") String type,
                                 @Field("content") String content);

    @FormUrlEncoded
    @POST("micropub")
    Call<Response> createNewPostWithTitle(@Header("Authorization") String token,
                                          @Field("h") String type,
                                          @Field("name") String title,
                                          @Field("content") String content);
}
