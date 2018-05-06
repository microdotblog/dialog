package com.dialogapp.dialog.repository;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;

import com.dialogapp.dialog.api.MicroblogService;
import com.dialogapp.dialog.db.MicroBlogDb;
import com.dialogapp.dialog.db.PostsDao;
import com.dialogapp.dialog.util.Event;
import com.dialogapp.dialog.util.InstantAppExecutors;
import com.dialogapp.dialog.util.Resource;
import com.dialogapp.dialog.util.Status;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class PostRequestManagerTest {
    private PostRequestManager requestManager;
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
        requestManager = new PostRequestManager(new InstantAppExecutors(), db, postsDao, microblogService);
    }

    @Test
    public void sendSuccessfulFollowRequest() throws IOException {
        Call<ResponseBody> response = createCall(false);
        when(microblogService.followUser("dialog")).thenReturn(response);

        LiveData<Event<Resource<Boolean>>> request = requestManager.followUser("dialog", true);
        verify(microblogService).followUser("dialog");
        verifyNoMoreInteractions(microblogService);

        Observer observer = mock(Observer.class);
        request.observeForever(observer);
        verifyNoMoreInteractions(microblogService);
        verify(postsDao).updateFollowState("dialog", true);
        assertThat(request.getValue().peekContent().data, is(true));
        verify(observer).onChanged(request.getValue());
    }

    @Test
    public void sendUnsuccessfulFollowRequest() throws IOException {
        Call<ResponseBody> response = createCall(true);
        when(microblogService.followUser("dialog")).thenReturn(response);

        LiveData<Event<Resource<Boolean>>> request = requestManager.followUser("dialog", true);
        verify(microblogService).followUser("dialog");
        verifyNoMoreInteractions(microblogService);

        Observer observer = mock(Observer.class);
        request.observeForever(observer);
        verifyNoMoreInteractions(microblogService);
        verifyZeroInteractions(postsDao);
        assertThat(request.getValue().peekContent().status, is(Status.ERROR));
        assertThat(request.getValue().peekContent().data, is(false));
        verify(observer).onChanged(request.getValue());
    }

    @Test
    public void sendSuccessfulFavoriteRequest() throws IOException {
        Call<ResponseBody> response = createCall(false);
        when(microblogService.favoritePostHaving("123")).thenReturn(response);

        LiveData<Event<Resource<Boolean>>> request = requestManager.sendFavoriteRequest("123", true);
        verify(microblogService).favoritePostHaving("123");
        verifyNoMoreInteractions(microblogService);

        Observer observer = mock(Observer.class);
        request.observeForever(observer);
        verifyNoMoreInteractions(microblogService);
        verify(postsDao).updateFavoriteState("123", true);
        verify(postsDao, never()).deleteFromFavorites("123");
        assertThat(request.getValue().peekContent().data, is(true));
        verify(observer).onChanged(request.getValue());

        Call<ResponseBody> response2 = createCall(false);
        when(microblogService.unfavoritePostHaving("123")).thenReturn(response2);

        reset(observer);
        request = requestManager.sendFavoriteRequest("123", false);
        request.observeForever(observer);
        verify(microblogService).unfavoritePostHaving("123");
        verifyNoMoreInteractions(microblogService);
        verify(postsDao).updateFavoriteState("123", false);
        verify(postsDao).deleteFromFavorites("123");
        assertThat(request.getValue().peekContent().data, is(true));
        verify(observer).onChanged(request.getValue());
    }

    @Test
    public void sendUnsuccessfulFavoriteRequest() throws IOException {
        Call<ResponseBody> response = createCall(true);
        when(microblogService.favoritePostHaving("123")).thenReturn(response);

        LiveData<Event<Resource<Boolean>>> request = requestManager.sendFavoriteRequest("123", true);
        verify(microblogService).favoritePostHaving("123");
        verifyNoMoreInteractions(microblogService);

        Observer observer = mock(Observer.class);
        request.observeForever(observer);
        verifyNoMoreInteractions(microblogService);
        verifyZeroInteractions(postsDao);
        assertThat(request.getValue().peekContent().status, is(Status.ERROR));
        assertThat(request.getValue().peekContent().data, is(false));
        verify(observer).onChanged(request.getValue());
    }

    private Call<ResponseBody> createCall(boolean fail) throws IOException {
        Response<ResponseBody> success = fail ? Response.error(500, ResponseBody.create(
                MediaType.parse("txt"), "")) : Response.success(ResponseBody.create(
                MediaType.parse("txt"), "{}"));
        Call call = mock(Call.class);
        when(call.execute()).thenReturn(success);
        //noinspection unchecked
        return call;
    }

    @Test
    public void sendSuccessfulReply() throws IOException {
        Call<ResponseBody> response = createCall(false);
        when(microblogService.replyToPost("123", "test")).thenReturn(response);

        LiveData<Event<Resource<Boolean>>> request = requestManager.sendReply("123", "test");
        verify(microblogService).replyToPost("123", "test");
        verifyNoMoreInteractions(microblogService);

        Observer observer = mock(Observer.class);
        request.observeForever(observer);
        verifyNoMoreInteractions(microblogService);
        verifyNoMoreInteractions(postsDao);
        assertThat(request.getValue().peekContent().data, is(true));
        verify(observer).onChanged(request.getValue());
    }

    @Test
    public void sendUnsuccessfulReply() throws IOException {
        Call<ResponseBody> response = createCall(true);
        when(microblogService.replyToPost("123", "test")).thenReturn(response);

        LiveData<Event<Resource<Boolean>>> request = requestManager.sendReply("123", "test");
        verify(microblogService).replyToPost("123", "test");
        verifyNoMoreInteractions(microblogService);

        Observer observer = mock(Observer.class);
        request.observeForever(observer);
        verifyNoMoreInteractions(microblogService);
        verifyNoMoreInteractions(postsDao);
        assertThat(request.getValue().peekContent().data, is(false));
        verify(observer).onChanged(request.getValue());
    }
}
