package com.dialogapp.dialog.ui.common;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;

import com.dialogapp.dialog.repository.PostRequestManager;
import com.dialogapp.dialog.util.Event;
import com.dialogapp.dialog.util.Resource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class RequestViewModelTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private RequestViewModel viewModel;
    private PostRequestManager postRequestManager;

    @Before
    public void setUp() {
        postRequestManager = mock(PostRequestManager.class);
        viewModel = new RequestViewModel(postRequestManager);
    }

    @Test
    public void testFollowRequest() {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Boolean> captor2 = ArgumentCaptor.forClass(Boolean.class);
        viewModel.getResponseFollow().observeForever(mock(Observer.class));

        viewModel.setFollowState("dialog", true);
        verify(postRequestManager).followUser(captor.capture(), captor2.capture());
        assertThat(captor.getValue(), is("dialog"));
        assertThat(captor2.getValue(), is(true));
    }

    @Test
    public void sendFollowResultToUI() {
        Event<Resource<Boolean>> eventResource = Event.createEvent(Resource.success(true));
        MutableLiveData<Event<Resource<Boolean>>> liveData = new MutableLiveData<>();
        when(postRequestManager.followUser("dialog", true)).thenReturn(liveData);

        Observer<Event<Resource<Boolean>>> observer = mock(Observer.class);
        viewModel.getResponseFollow().observeForever(observer);
        viewModel.setFollowState("dialog", true);
        verify(observer, never()).onChanged(any(Event.class));

        liveData.setValue(eventResource);
        verify(observer).onChanged(eventResource);
    }

    @Test
    public void testFavoriteRequest() {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Boolean> captor2 = ArgumentCaptor.forClass(Boolean.class);
        viewModel.getResponseFavorite().observeForever(mock(Observer.class));

        viewModel.setFavoriteState("123", true);
        verify(postRequestManager).sendFavoriteRequest(captor.capture(), captor2.capture());
        assertThat(captor.getValue(), is("123"));
        assertThat(captor2.getValue(), is(true));
    }

    @Test
    public void sendFavoriteResultToUI() {
        Event<Resource<Boolean>> eventResource = Event.createEvent(Resource.success(true));
        MutableLiveData<Event<Resource<Boolean>>> liveData = new MutableLiveData<>();
        when(postRequestManager.sendFavoriteRequest("123", true)).thenReturn(liveData);

        Observer<Event<Resource<Boolean>>> observer = mock(Observer.class);
        viewModel.getResponseFavorite().observeForever(observer);
        viewModel.setFavoriteState("123", true);
        verify(observer, never()).onChanged(any(Event.class));

        liveData.setValue(eventResource);
        verify(observer).onChanged(eventResource);
    }

    @Test
    public void testReply() {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> captor2 = ArgumentCaptor.forClass(String.class);
        viewModel.getResponseReply().observeForever(mock(Observer.class));

        viewModel.sendReply("123", "test");
        verify(postRequestManager).sendReply(captor.capture(), captor2.capture());
        assertThat(captor.getValue(), is("123"));
        assertThat(captor2.getValue(), is("test"));
    }

    @Test
    public void testReplyRetry() {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> captor2 = ArgumentCaptor.forClass(String.class);
        viewModel.getResponseReply().observeForever(mock(Observer.class));

        viewModel.sendReply("123", "test");
        verify(postRequestManager).sendReply(captor.capture(), captor2.capture());
        assertThat(captor.getValue(), is("123"));
        assertThat(captor2.getValue(), is("test"));
        reset(postRequestManager);
        viewModel.retryReply();
        verify(postRequestManager).sendReply(captor.capture(), captor2.capture());
        assertThat(captor.getValue(), is("123"));
        assertThat(captor2.getValue(), is("test"));
    }

    @Test
    public void sendReplyResultToUI() {
        Event<Resource<Boolean>> eventResource = Event.createEvent(Resource.success(true));
        MutableLiveData<Event<Resource<Boolean>>> liveData = new MutableLiveData<>();
        when(postRequestManager.sendReply("123", "test")).thenReturn(liveData);

        Observer<Event<Resource<Boolean>>> observer = mock(Observer.class);
        viewModel.getResponseReply().observeForever(observer);
        viewModel.sendReply("123", "test");
        verify(observer, never()).onChanged(any(Event.class));

        liveData.setValue(eventResource);
        verify(observer).onChanged(eventResource);
    }
}
