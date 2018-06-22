package com.dialogapp.dialog.api;

import android.arch.lifecycle.LiveData;

import com.dialogapp.dialog.model.FollowingAccountInfo;
import com.dialogapp.dialog.model.AccountResponse;
import com.dialogapp.dialog.model.MediaEndPoint;
import com.dialogapp.dialog.model.MicroBlogResponse;
import com.dialogapp.dialog.model.VerifiedAccount;

import java.util.List;

import okhttp3.ResponseBody;
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
    LiveData<ApiResponse<MicroBlogResponse>> getFeaturedPosts();

    @GET("posts/discover/{topic}")
    LiveData<ApiResponse<MicroBlogResponse>> getFeaturedPosts(@Path("topic") String topic);

    @GET("posts/{username}")
    LiveData<ApiResponse<MicroBlogResponse>> getPostsByUsername(@Path("username") String username);

    @GET("posts/conversation")
    LiveData<ApiResponse<MicroBlogResponse>> getConversation(@Query("id") String id);

    @GET("posts/check")
    Call<MicroBlogResponse> getNewPostsSince(@Query("since_id") long since_id);

    @GET("users/following/{username}")
    LiveData<ApiResponse<List<FollowingAccountInfo>>> getFollowing(@Path("username") String username);

    // POST

    @FormUrlEncoded
    @Headers("NO-AUTH: true")
    @POST("account/verify")
    LiveData<ApiResponse<VerifiedAccount>> verifyToken(@Field("token") String token);

    @FormUrlEncoded
    @POST("posts/favorites")
    Call<ResponseBody> favoritePostHaving(@Field("id") String id);

    @FormUrlEncoded
    @POST("posts/reply")
    Call<ResponseBody> replyToPost(@Field("id") String id, @Field("text") String content);

    @FormUrlEncoded
    @POST("users/follow")
    Call<ResponseBody> followUser(@Field("username") String user);

    @FormUrlEncoded
    @POST("users/unfollow")
    Call<ResponseBody> unfollowUser(@Field("username") String user);

    // DELETE

    @DELETE("posts/favorites/{id}")
    Call<ResponseBody> unfavoritePostHaving(@Path("id") String id);

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
