package com.dialogapp.dialog.repository;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dialogapp.dialog.AppExecutors;
import com.dialogapp.dialog.api.ApiResponse;
import com.dialogapp.dialog.api.MicroblogService;
import com.dialogapp.dialog.db.MicroBlogDb;
import com.dialogapp.dialog.db.PostsDao;
import com.dialogapp.dialog.model.Item;
import com.dialogapp.dialog.model.MicroBlogResponse;
import com.dialogapp.dialog.util.Resource;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PostsRepository {
    private final AppExecutors appExecutors;
    private final MicroblogService microblogService;
    private MicroBlogDb db;
    private final PostsDao postsDao;

    private RateLimiter<String> endpointRateLimit = new RateLimiter<>(2, TimeUnit.MINUTES);

    @Inject
    public PostsRepository(AppExecutors appExecutors, MicroBlogDb db, PostsDao postsDao, MicroblogService microblogService) {
        this.appExecutors = appExecutors;
        this.db = db;
        this.postsDao = postsDao;
        this.microblogService = microblogService;
    }

    public LiveData<Resource<List<Item>>> loadTimeline() {
        return new NetworkBoundResource<List<Item>, MicroBlogResponse>(appExecutors) {
            @Override
            protected boolean shouldFetch(@Nullable List<Item> dbData) {
                return dbData == null || dbData.isEmpty() || endpointRateLimit.shouldFetch(Endpoints.TIMELINE);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<MicroBlogResponse>> createCall() {
                return microblogService.getTimeLine(null);
            }

            @Override
            protected MicroBlogResponse processResponse(ApiResponse<MicroBlogResponse> response) {
                for (Item item : response.body.items) {
                    item.setEndpoint(Endpoints.TIMELINE);
                }
                return response.body;
            }

            @Override
            protected void saveCallResult(@NonNull MicroBlogResponse response) {
                db.beginTransaction();
                try {
                    db.postsDao().deletePosts(Endpoints.TIMELINE);
                    db.postsDao().insertPosts(response.items);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            }

            @NonNull
            @Override
            protected LiveData<List<Item>> loadFromDb() {
                return postsDao.loadEndpoint(Endpoints.TIMELINE);
            }

            @Override
            protected void onFetchFailed() {
                endpointRateLimit.reset(Endpoints.TIMELINE);
            }
        }.asLiveData();
    }

    public LiveData<Resource<List<Item>>> loadMentions() {
        return new NetworkBoundResource<List<Item>, MicroBlogResponse>(appExecutors) {
            @Override
            protected boolean shouldFetch(@Nullable List<Item> dbData) {
                return dbData == null || dbData.isEmpty() || endpointRateLimit.shouldFetch(Endpoints.MENTIONS);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<MicroBlogResponse>> createCall() {
                return microblogService.getMentions(null);
            }

            @Override
            protected MicroBlogResponse processResponse(ApiResponse<MicroBlogResponse> response) {
                for (Item item : response.body.items) {
                    item.setEndpoint(Endpoints.MENTIONS);
                }
                return response.body;
            }

            @Override
            protected void saveCallResult(@NonNull MicroBlogResponse response) {
                db.beginTransaction();
                try {
                    db.postsDao().deletePosts(Endpoints.MENTIONS);
                    db.postsDao().insertPosts(response.items);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            }

            @NonNull
            @Override
            protected LiveData<List<Item>> loadFromDb() {
                return postsDao.loadEndpoint(Endpoints.MENTIONS);
            }

            @Override
            protected void onFetchFailed() {
                endpointRateLimit.reset(Endpoints.MENTIONS);
            }
        }.asLiveData();
    }

    public LiveData<Resource<List<Item>>> loadFavorites() {
        return new NetworkBoundResource<List<Item>, MicroBlogResponse>(appExecutors) {
            @Override
            protected boolean shouldFetch(@Nullable List<Item> dbData) {
                return dbData == null || dbData.isEmpty() || endpointRateLimit.shouldFetch(Endpoints.FAVORITES);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<MicroBlogResponse>> createCall() {
                return microblogService.getFavorites();
            }

            @Override
            protected MicroBlogResponse processResponse(ApiResponse<MicroBlogResponse> response) {
                for (Item item : response.body.items) {
                    item.setEndpoint(Endpoints.FAVORITES);
                }
                return response.body;
            }

            @Override
            protected void saveCallResult(@NonNull MicroBlogResponse response) {
                postsDao.insertPosts(response.items);
            }

            @NonNull
            @Override
            protected LiveData<List<Item>> loadFromDb() {
                return postsDao.loadEndpoint(Endpoints.FAVORITES);
            }

            @Override
            protected void onFetchFailed() {
                endpointRateLimit.reset(Endpoints.FAVORITES);
            }
        }.asLiveData();
    }

    public LiveData<Resource<MicroBlogResponse>> loadPostsByUsername(String username) {
        return new NetworkBoundResource<MicroBlogResponse, MicroBlogResponse>(appExecutors) {
            @Override
            protected boolean shouldFetch(@Nullable MicroBlogResponse dbData) {
                return dbData == null || endpointRateLimit.shouldFetch(username);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<MicroBlogResponse>> createCall() {
                return microblogService.getPostsByUsername(username);
            }

            @Override
            protected void saveCallResult(@NonNull MicroBlogResponse response) {
                postsDao.insertMicroblogData(response);
            }

            @NonNull
            @Override
            protected LiveData<MicroBlogResponse> loadFromDb() {
                return postsDao.loadMicroblogData(username);
            }

            @Override
            protected void onFetchFailed() {
                endpointRateLimit.reset(username);
            }
        }.asLiveData();
    }

    public LiveData<Resource<List<Item>>> loadConversation(String id) {
        return new NetworkBoundResource<List<Item>, MicroBlogResponse>(appExecutors) {
            List<Item> responseData;

            @Override
            protected boolean shouldFetch(@Nullable List<Item> dbData) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<MicroBlogResponse>> createCall() {
                return microblogService.getConversation(id);
            }

            @Override
            protected void saveCallResult(@NonNull MicroBlogResponse response) {
                responseData = response.items;
            }

            @NonNull
            @Override
            protected LiveData<List<Item>> loadFromDb() {
                return new LiveData<List<Item>>() {
                    @Override
                    protected void onActive() {
                        super.onActive();
                        setValue(responseData);
                    }
                };
            }
        }.asLiveData();
    }

    public LiveData<Resource<List<Item>>> loadDiscover(String topic) {
        return new NetworkBoundResource<List<Item>, MicroBlogResponse>(appExecutors) {
            @Override
            protected boolean shouldFetch(@Nullable List<Item> dbData) {
                return dbData == null || dbData.isEmpty() || endpointRateLimit.shouldFetch("discover");
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<MicroBlogResponse>> createCall() {
                if (topic == null)
                    return microblogService.getFeaturedPosts();
                else
                    return microblogService.getFeaturedPosts(topic);
            }

            @Override
            protected MicroBlogResponse processResponse(ApiResponse<MicroBlogResponse> response) {
                for (Item item : response.body.items) {
                    item.setEndpoint(Endpoints.DISCOVER);
                }
                return response.body;
            }

            @Override
            protected void saveCallResult(@NonNull MicroBlogResponse response) {
                db.beginTransaction();
                try {
                    db.postsDao().deletePosts(Endpoints.DISCOVER);
                    db.postsDao().insertPosts(response.items);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            }

            @NonNull
            @Override
            protected LiveData<List<Item>> loadFromDb() {
                return postsDao.loadEndpoint(Endpoints.DISCOVER);
            }

            @Override
            protected void onFetchFailed() {
                endpointRateLimit.reset("discover");
            }
        }.asLiveData();
    }
}
