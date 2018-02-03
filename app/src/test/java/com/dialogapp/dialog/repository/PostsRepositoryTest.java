package com.dialogapp.dialog.repository;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;

import com.dialogapp.dialog.TestUtil;
import com.dialogapp.dialog.api.ApiResponse;
import com.dialogapp.dialog.api.MicroblogService;
import com.dialogapp.dialog.db.MicroBlogDb;
import com.dialogapp.dialog.db.PostsDao;
import com.dialogapp.dialog.model.Item;
import com.dialogapp.dialog.model.Post;
import com.dialogapp.dialog.util.InstantAppExecutors;
import com.dialogapp.dialog.util.Resource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static com.dialogapp.dialog.util.ApiUtil.successCall;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
@RunWith(JUnit4.class)
public class PostsRepositoryTest {
    private PostsRepository repository;
    private PostsDao postsDao;
    private MicroblogService microblogService;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() {
        postsDao = mock(PostsDao.class);
        microblogService = mock(MicroblogService.class);
        MicroBlogDb db = mock(MicroBlogDb.class);
        when(db.postsDao()).thenReturn(postsDao);
        repository = new PostsRepository(new InstantAppExecutors(), db, postsDao, microblogService);
    }

    @Test
    public void loadTimelineDataFromNetwork() throws IOException {
        MutableLiveData<List<Post>> timelineDbData = new MutableLiveData<>();
        when(postsDao.loadTimeline()).thenReturn(timelineDbData);

        List<Item> testTimelineData = TestUtil.readFromJson(getClass().getClassLoader(), "timeline.json");
        LiveData<ApiResponse<List<Item>>> callTimeline = successCall(testTimelineData);
        when(microblogService.getTimeLine()).thenReturn(callTimeline);

        LiveData<Resource<List<Post>>> repoData = repository.loadTimeline();
        verify(postsDao).loadTimeline();
        verifyNoMoreInteractions(microblogService);

        Observer observer = mock(Observer.class);
        repoData.observeForever(observer);
        verifyNoMoreInteractions(microblogService);
        verify(observer).onChanged(Resource.loading(null));

        MutableLiveData<List<Post>> updatedTimelineData = new MutableLiveData<>();
        when(postsDao.loadTimeline()).thenReturn(updatedTimelineData);
        timelineDbData.postValue(null);
        verify(microblogService).getTimeLine();
        verify(postsDao).insertTimeline(anyList());

        List<Post> timelineData = testTimelineData.stream().map(x -> new Post(x.getId(), x.getUrl(), x.getContentHtml(),
                x.getDatePublished(), x.getMicroblog().dateRelative, x.getMicroblog().isDeletable,
                x.getMicroblog().isFavorite, x.getAuthor().name, x.getAuthor().url, x.getAuthor().avatar,
                x.getAuthor().microblog.username)).collect(Collectors.toList());

        updatedTimelineData.postValue(timelineData);
        verify(observer).onChanged(Resource.success(timelineData));
    }

    @Test
    public void loadMentionsDataFromNetwork() throws IOException {
        MutableLiveData<List<Post>> mentionsDbData = new MutableLiveData<>();
        when(postsDao.loadMentions()).thenReturn(mentionsDbData);

        List<Item> testMentionsData = TestUtil.readFromJson(getClass().getClassLoader(), "mentions.json");
        LiveData<ApiResponse<List<Item>>> callMentions = successCall(testMentionsData);
        when(microblogService.getMentions()).thenReturn(callMentions);

        LiveData<Resource<List<Post>>> repoData = repository.loadMentions();
        verify(postsDao).loadMentions();
        verifyNoMoreInteractions(microblogService);

        Observer observer = mock(Observer.class);
        repoData.observeForever(observer);
        verifyNoMoreInteractions(microblogService);
        verify(observer).onChanged(Resource.loading(null));

        MutableLiveData<List<Post>> updatedMentionsData = new MutableLiveData<>();
        when(postsDao.loadMentions()).thenReturn(updatedMentionsData);
        mentionsDbData.postValue(null);
        verify(microblogService).getMentions();
        verify(postsDao).insertMentions(anyList());

        List<Post> mentionsData = testMentionsData.stream().map(x -> new Post(x.getId(), x.getUrl(), x.getContentHtml(),
                x.getDatePublished(), x.getMicroblog().dateRelative, x.getMicroblog().isDeletable,
                x.getMicroblog().isFavorite, x.getAuthor().name, x.getAuthor().url, x.getAuthor().avatar,
                x.getAuthor().microblog.username)).collect(Collectors.toList());

        updatedMentionsData.postValue(mentionsData);
        verify(observer).onChanged(Resource.success(mentionsData));
    }
}
