package com.dialogapp.dialog.repository;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.dialogapp.dialog.AppExecutors;
import com.dialogapp.dialog.api.ApiResponse;
import com.dialogapp.dialog.api.MicroblogService;
import com.dialogapp.dialog.db.MicroBlogDb;
import com.dialogapp.dialog.db.PostsDao;
import com.dialogapp.dialog.util.Event;
import com.dialogapp.dialog.util.Resource;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.ResponseBody;
import retrofit2.Call;
import timber.log.Timber;

@Singleton
public class PostRequestManager {
    private AppExecutors appExecutors;
    private MicroBlogDb db;
    private PostsDao postsDao;
    private MicroblogService microblogService;

    @Inject
    public PostRequestManager(AppExecutors appExecutors, MicroBlogDb db, PostsDao postsDao, MicroblogService microblogService) {
        this.appExecutors = appExecutors;
        this.db = db;
        this.postsDao = postsDao;
        this.microblogService = microblogService;
    }

    public LiveData<Event<Resource<Boolean>>> followUser(String username, boolean shouldFollow) {
        return new NetworkBoundRequestResource<ResponseBody>(appExecutors) {
            @NonNull
            @Override
            protected Call<ResponseBody> createCall() {
                if (shouldFollow)
                    return microblogService.followUser(username);
                else
                    return microblogService.unfollowUser(username);
            }

            @Override
            protected boolean wasExpectedResponse(ApiResponse<ResponseBody> apiResponse) {
                try {
                    return apiResponse.body.string().equals("{}");
                } catch (IOException e) {
                    Timber.e("Unexpected response : %s", e.getMessage());
                    return false;
                }
            }

            @Override
            protected void performDbOperation() {
                db.beginTransaction();
                try {
                    if (!shouldFollow) {
                        postsDao.deleteTimelinePostsOf(username);
                    }
                    postsDao.updateFollowState(username, shouldFollow);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            }
        }.asEventLiveData();
    }

    public LiveData<Event<Resource<Boolean>>> sendFavoriteRequest(String id, boolean favoriteValue) {
        return new NetworkBoundRequestResource<ResponseBody>(appExecutors) {
            @NonNull
            @Override
            protected Call<ResponseBody> createCall() {
                if (favoriteValue)
                    return microblogService.favoritePostHaving(id);
                else
                    return microblogService.unfavoritePostHaving(id);
            }

            @Override
            protected boolean wasExpectedResponse(ApiResponse<ResponseBody> apiResponse) {
                try {
                    return apiResponse.body.string().equals("{}");
                } catch (IOException e) {
                    Timber.e("Unexpected response : %s", e.getMessage());
                    return false;
                }
            }

            @Override
            protected void performDbOperation() {
                db.beginTransaction();
                try {
                    postsDao.updateFavoriteState(id, favoriteValue);
                    if (!favoriteValue)
                        postsDao.deleteFromFavorites(id);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            }
        }.asEventLiveData();
    }

    public LiveData<Event<Resource<Boolean>>> sendReply(String id, String content) {
        return new NetworkBoundRequestResource<ResponseBody>(appExecutors) {
            @NonNull
            @Override
            protected Call<ResponseBody> createCall() {
                return microblogService.replyToPost(id, content);
            }

            @Override
            protected boolean wasExpectedResponse(ApiResponse<ResponseBody> apiResponse) {
                try {
                    return apiResponse.body.string().equals("{}");
                } catch (IOException e) {
                    Timber.e("Unexpected response : %s", e.getMessage());
                    return false;
                }
            }
        }.asEventLiveData();
    }
}
