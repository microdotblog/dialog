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
import com.dialogapp.dialog.util.InstantAppExecutors;
import com.dialogapp.dialog.util.Resource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

import static com.dialogapp.dialog.util.ApiUtil.successCall;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

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
        repository = new PostsRepository(new InstantAppExecutors(), postsDao, microblogService);
    }

    @Test
    public void loadTimelineDataFromNetwork() {
        MutableLiveData<List<Item>> timelineDbData = new MutableLiveData<>();
        when(postsDao.loadEndpoint(Endpoints.TIMELINE)).thenReturn(timelineDbData);

        List<Item> testTimelineData = TestUtil.createListOfEndpoint(Endpoints.TIMELINE);
        LiveData<ApiResponse<List<Item>>> callTimeline = successCall(testTimelineData);
        when(microblogService.getTimeLine()).thenReturn(callTimeline);

        LiveData<Resource<List<Item>>> repoData = repository.loadTimeline();
        verify(postsDao).loadEndpoint(Endpoints.TIMELINE);
        verifyNoMoreInteractions(microblogService);

        Observer observer = mock(Observer.class);
        repoData.observeForever(observer);
        verifyNoMoreInteractions(microblogService);
        verify(observer).onChanged(Resource.loading(null));

        MutableLiveData<List<Item>> updatedTimelineData = new MutableLiveData<>();
        when(postsDao.loadEndpoint(Endpoints.TIMELINE)).thenReturn(updatedTimelineData);

        timelineDbData.postValue(null);
        verify(microblogService).getTimeLine();
        verify(postsDao).insertTimeline(testTimelineData);

        updatedTimelineData.postValue(testTimelineData);
        verify(observer).onChanged(Resource.success(testTimelineData));
    }

    @Test
    public void loadMentionsDataFromNetwork() {
        MutableLiveData<List<Item>> mentionsDbData = new MutableLiveData<>();
        when(postsDao.loadEndpoint(Endpoints.MENTIONS)).thenReturn(mentionsDbData);

        List<Item> testMentionsData = TestUtil.createListOfEndpoint(Endpoints.MENTIONS);
        LiveData<ApiResponse<List<Item>>> callMentions = successCall(testMentionsData);
        when(microblogService.getMentions()).thenReturn(callMentions);

        LiveData<Resource<List<Item>>> repoData = repository.loadMentions();
        verify(postsDao).loadEndpoint(Endpoints.MENTIONS);
        verifyNoMoreInteractions(microblogService);

        Observer observer = mock(Observer.class);
        repoData.observeForever(observer);
        verifyNoMoreInteractions(microblogService);
        verify(observer).onChanged(Resource.loading(null));

        MutableLiveData<List<Item>> updatedMentionsData = new MutableLiveData<>();
        when(postsDao.loadEndpoint(Endpoints.MENTIONS)).thenReturn(updatedMentionsData);

        mentionsDbData.postValue(null);
        verify(microblogService).getMentions();
        verify(postsDao).insertMentions(testMentionsData);

        updatedMentionsData.postValue(testMentionsData);
        verify(observer).onChanged(Resource.success(testMentionsData));
    }
}
