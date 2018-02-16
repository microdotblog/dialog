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

import java.io.IOException;
import java.util.List;

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
        repository = new PostsRepository(new InstantAppExecutors(), postsDao, microblogService);
    }

    @Test
    public void loadTimelineDataFromNetwork() throws IOException {
        MutableLiveData<List<Item>> timelineDbData = new MutableLiveData<>();
        when(postsDao.loadEndpoint(Endpoints.TIMELINE)).thenReturn(timelineDbData);

        List<Item> testTimelineData = TestUtil.readFromJson(getClass().getClassLoader(), "timeline.json");
        LiveData<ApiResponse<List<Item>>> callTimeline = successCall(testTimelineData);
        when(microblogService.getTimeLine()).thenReturn(callTimeline);

        LiveData<Resource<List<Item>>> repoData = repository.loadTimeline(true);
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
        verify(postsDao).insertPosts(anyList());

        updatedTimelineData.postValue(testTimelineData);
        verify(observer).onChanged(Resource.success(testTimelineData));
    }

    @Test
    public void loadMentionsDataFromNetwork() throws IOException {
        MutableLiveData<List<Item>> mentionsDbData = new MutableLiveData<>();
        when(postsDao.loadEndpoint(Endpoints.MENTIONS)).thenReturn(mentionsDbData);

        List<Item> testMentionsData = TestUtil.readFromJson(getClass().getClassLoader(), "mentions.json");
        LiveData<ApiResponse<List<Item>>> callMentions = successCall(testMentionsData);
        when(microblogService.getMentions()).thenReturn(callMentions);

        LiveData<Resource<List<Item>>> repoData = repository.loadMentions(true);
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
        verify(postsDao).insertPosts(anyList());

        updatedMentionsData.postValue(testMentionsData);
        verify(observer).onChanged(Resource.success(testMentionsData));
    }

    @Test
    public void loadFavoritesDataFromNetwork() throws IOException {
        MutableLiveData<List<Item>> favoritesDbData = new MutableLiveData<>();
        when(postsDao.loadEndpoint(Endpoints.FAVORITES)).thenReturn(favoritesDbData);

        List<Item> testFavoritesData = TestUtil.readFromJson(getClass().getClassLoader(), "favorites.json");
        LiveData<ApiResponse<List<Item>>> callFavorites = successCall(testFavoritesData);
        when(microblogService.getFavorites()).thenReturn(callFavorites);

        LiveData<Resource<List<Item>>> repoData = repository.loadFavorites(true);
        verify(postsDao).loadEndpoint(Endpoints.FAVORITES);
        verifyNoMoreInteractions(microblogService);

        Observer observer = mock(Observer.class);
        repoData.observeForever(observer);
        verifyNoMoreInteractions(microblogService);
        verify(observer).onChanged(Resource.loading(null));

        MutableLiveData<List<Item>> updatedFavoritesData = new MutableLiveData<>();
        when(postsDao.loadEndpoint(Endpoints.FAVORITES)).thenReturn(updatedFavoritesData);
        favoritesDbData.postValue(null);
        verify(microblogService).getFavorites();
        verify(postsDao).insertPosts(anyList());

        updatedFavoritesData.postValue(testFavoritesData);
        verify(observer).onChanged(Resource.success(testFavoritesData));
    }
}
