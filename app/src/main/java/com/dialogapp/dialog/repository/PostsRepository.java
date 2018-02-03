package com.dialogapp.dialog.repository;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dialogapp.dialog.AppExecutors;
import com.dialogapp.dialog.api.ApiResponse;
import com.dialogapp.dialog.api.MicroblogService;
import com.dialogapp.dialog.db.MicroBlogDb;
import com.dialogapp.dialog.db.PostsDao;
import com.dialogapp.dialog.db.entity.Mention;
import com.dialogapp.dialog.db.entity.Timeline;
import com.dialogapp.dialog.model.Author;
import com.dialogapp.dialog.model.Item;
import com.dialogapp.dialog.model.Post;
import com.dialogapp.dialog.util.Resource;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PostsRepository {

    private final AppExecutors appExecutors;
    private final MicroblogService microblogService;
    private final PostsDao postsDao;
    private final MicroBlogDb microBlogDb;

    @Inject
    public PostsRepository(AppExecutors appExecutors, MicroBlogDb microBlogDb, PostsDao postsDao,
                           MicroblogService microblogService) {
        this.appExecutors = appExecutors;
        this.microBlogDb = microBlogDb;
        this.postsDao = postsDao;
        this.microblogService = microblogService;
    }

    public LiveData<Resource<List<Post>>> loadTimeline() {
        return new NetworkBoundResource<List<Post>, List<Item>>(appExecutors) {
            @Override
            protected boolean shouldFetch(@Nullable List<Post> dbData) {
                return dbData == null || dbData.isEmpty();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<Item>>> createCall() {
                return microblogService.getTimeLine();
            }

            @Override
            protected void saveCallResult(@NonNull List<Item> items) {
                List<Author> authors = new ArrayList<>();
                for (Item item : items) {
                    authors.add(item.getAuthor());
                }

                microBlogDb.beginTransaction();
                try {
                    postsDao.insertAuthors(authors);

                    for (Item item : items) {
                        item.post_author_id = postsDao.getAuthorIdOf(item.getAuthor().microblog.username);
                    }
                    postsDao.insertPosts(items);

                    List<Timeline> timelineIds = new ArrayList<>();
                    for (Item item : items) {
                        timelineIds.add(new Timeline(item.getId(), postsDao.getPostIdOf(item.getUrl())));
                    }
                    postsDao.insertTimeline(timelineIds);

                    microBlogDb.setTransactionSuccessful();
                } finally {
                    microBlogDb.endTransaction();
                }
            }

            @NonNull
            @Override
            protected LiveData<List<Post>> loadFromDb() {
                return postsDao.loadTimeline();
            }
        }.asLiveData();
    }

    public LiveData<Resource<List<Post>>> loadMentions() {
        return new NetworkBoundResource<List<Post>, List<Item>>(appExecutors) {
            @Override
            protected boolean shouldFetch(@Nullable List<Post> dbData) {
                return dbData == null || dbData.isEmpty();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<Item>>> createCall() {
                return microblogService.getMentions();
            }

            @Override
            protected void saveCallResult(@NonNull List<Item> items) {
                List<Author> authors = new ArrayList<>();
                for (Item item : items) {
                    authors.add(item.getAuthor());
                }

                microBlogDb.beginTransaction();
                try {
                    postsDao.insertAuthors(authors);

                    for (Item item : items) {
                        item.post_author_id = postsDao.getAuthorIdOf(item.getAuthor().microblog.username);
                    }
                    postsDao.insertPosts(items);

                    List<Mention> mentionsIds = new ArrayList<>();
                    for (Item item : items) {
                        mentionsIds.add(new Mention(item.getId(), postsDao.getPostIdOf(item.getUrl())));
                    }
                    postsDao.insertMentions(mentionsIds);

                    microBlogDb.setTransactionSuccessful();
                } finally {
                    microBlogDb.endTransaction();
                }
            }

            @NonNull
            @Override
            protected LiveData<List<Post>> loadFromDb() {
                return postsDao.loadMentions();
            }
        }.asLiveData();
    }
}
