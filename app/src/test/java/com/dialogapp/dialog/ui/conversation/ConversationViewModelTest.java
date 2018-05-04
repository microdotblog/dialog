package com.dialogapp.dialog.ui.conversation;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;

import com.dialogapp.dialog.TestUtil;
import com.dialogapp.dialog.model.Item;
import com.dialogapp.dialog.model.MicroBlogResponse;
import com.dialogapp.dialog.repository.PostsRepository;
import com.dialogapp.dialog.util.Resource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class ConversationViewModelTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private ConversationViewModel viewModel;
    private PostsRepository postsRepository;

    @Before
    public void setUp() {
        postsRepository = mock(PostsRepository.class);
        viewModel = new ConversationViewModel(postsRepository);
    }

    @Test
    public void testLoading() {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        viewModel.getPosts().observeForever(mock(Observer.class));
        viewModel.setPostId("123");
        verify(postsRepository).loadConversation(captor.capture());
        assertThat(captor.getValue(), is("123"));
    }

    @Test
    public void testRefresh() {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        viewModel.getPosts().observeForever(mock(Observer.class));
        verifyNoMoreInteractions(postsRepository);

        viewModel.setPostId("123");
        viewModel.refresh();
        verify(postsRepository, times(2)).loadConversation(captor.capture());
        assertThat(captor.getValue(), is("123"));
    }

    @Test
    public void sendResultToUI() throws IOException {
        MutableLiveData<Resource<List<Item>>> data = new MutableLiveData<>();
        MicroBlogResponse response = TestUtil.readFromJson(getClass().getClassLoader(), "response.json");
        Resource<List<Item>> listResource = Resource.success(response.items);
        when(postsRepository.loadConversation("123")).thenReturn(data);

        Observer<Resource<List<Item>>> observer = mock(Observer.class);
        viewModel.getPosts().observeForever(observer);
        data.setValue(listResource);
        viewModel.setPostId("123");
        verify(observer).onChanged(listResource);
    }
}