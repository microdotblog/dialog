package com.dialogapp.dialog.repository;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dialogapp.dialog.AppExecutors;
import com.dialogapp.dialog.api.ApiResponse;
import com.dialogapp.dialog.api.MicroblogService;
import com.dialogapp.dialog.db.PostsDao;
import com.dialogapp.dialog.model.Item;
import com.dialogapp.dialog.util.Resource;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PostsRepository {

    private final long THRESHOLDMILLIS = 120000;

    private final AppExecutors appExecutors;
    private final MicroblogService microblogService;
    private final PostsDao postsDao;

    private ConcurrentHashMap<String, Long> requestTimings = new ConcurrentHashMap<>();

    @Inject
    public PostsRepository(AppExecutors appExecutors, PostsDao postsDao, MicroblogService microblogService) {
        this.appExecutors = appExecutors;
        this.postsDao = postsDao;
        this.microblogService = microblogService;
    }

    public LiveData<Resource<List<Item>>> loadTimeline(boolean refresh) {
        return new NetworkBoundResource<List<Item>, List<Item>>(appExecutors) {
            @Override
            protected boolean shouldFetch(@Nullable List<Item> dbData) {
                return shouldRefresh(dbData, refresh, Endpoints.TIMELINE);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<Item>>> createCall() {
                return microblogService.getTimeLine();
            }

            @Override
            protected void saveCallResult(@NonNull List<Item> items) {
                requestTimings.put(Endpoints.TIMELINE, System.currentTimeMillis());
                for (Item item : items) {
                    item.setEndpoint(Endpoints.TIMELINE);
                }

                if (refresh)
                    postsDao.deletePosts(Endpoints.TIMELINE);
                postsDao.insertPosts(items);
            }

            @NonNull
            @Override
            protected LiveData<List<Item>> loadFromDb() {
                return postsDao.loadEndpoint(Endpoints.TIMELINE);
            }
        }.asLiveData();
    }

    public LiveData<Resource<List<Item>>> loadMentions(boolean refresh) {
        return new NetworkBoundResource<List<Item>, List<Item>>(appExecutors) {
            @Override
            protected boolean shouldFetch(@Nullable List<Item> dbData) {
                return shouldRefresh(dbData, refresh, Endpoints.MENTIONS);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<Item>>> createCall() {
                return microblogService.getMentions();
            }

            @Override
            protected void saveCallResult(@NonNull List<Item> items) {
                requestTimings.put(Endpoints.MENTIONS, System.currentTimeMillis());
                for (Item item : items) {
                    item.setEndpoint(Endpoints.MENTIONS);
                }

                if (refresh)
                    postsDao.deletePosts(Endpoints.MENTIONS);
                postsDao.insertPosts(items);
            }

            @NonNull
            @Override
            protected LiveData<List<Item>> loadFromDb() {
                return postsDao.loadEndpoint(Endpoints.MENTIONS);
            }
        }.asLiveData();
    }

    private boolean shouldRefresh(List<Item> dbData, boolean refresh, String endpoint) {
        if (refresh) {
            return hasWaitTimeExpired(endpoint);
        } else {
            return dbData == null || dbData.isEmpty();
        }
    }

    private boolean hasWaitTimeExpired(String endpoint) {
        if (!requestTimings.containsKey(endpoint)) {
            return true;
        } else {
            return (System.currentTimeMillis() - requestTimings.get(endpoint)) >= THRESHOLDMILLIS;
        }
    }
}
