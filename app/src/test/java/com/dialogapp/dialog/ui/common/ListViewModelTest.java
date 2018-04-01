package com.dialogapp.dialog.ui.common;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;

import com.dialogapp.dialog.TestUtil;
import com.dialogapp.dialog.model.Item;
import com.dialogapp.dialog.model.MicroBlogResponse;
import com.dialogapp.dialog.repository.PostsRepository;
import com.dialogapp.dialog.ui.base.BaseListViewModel;
import com.dialogapp.dialog.util.Resource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class ListViewModelTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private ListViewModel viewModel;
    private PostsRepository postsRepository;

    @Before
    public void setUp() throws IOException {
        postsRepository = mock(PostsRepository.class);

        MutableLiveData<Resource<List<Item>>> data = new MutableLiveData<>();
        MicroBlogResponse response = TestUtil.readFromJson(getClass().getClassLoader(), "response.json");
        Resource<List<Item>> listResource = Resource.success(response.items);
        data.setValue(listResource);
        when(postsRepository.loadFavorites()).thenReturn(data);
        when(postsRepository.loadMentions()).thenReturn(data);
        when(postsRepository.loadTimeline()).thenReturn(data);
        when(postsRepository.loadConversation("123")).thenReturn(data);

        viewModel = new ListViewModel(postsRepository);
    }

    @Test
    public void testLoading() {
        assertThat(viewModel.getPosts().getValue(), nullValue());
        viewModel.getPosts().observeForever(mock(Observer.class));
        viewModel.setView(BaseListViewModel.TIMELINE, null);
        assertThat(viewModel.getPosts().getValue(), notNullValue());
        verify(postsRepository).loadTimeline();

        viewModel.setView(BaseListViewModel.MENTIONS, null);
        assertThat(viewModel.getPosts().getValue(), notNullValue());
        verify(postsRepository).loadMentions();

        viewModel.setView(BaseListViewModel.FAVORITES, null);
        assertThat(viewModel.getPosts().getValue(), notNullValue());
        verify(postsRepository).loadMentions();

        viewModel.setView(BaseListViewModel.CONVERSATION, "123");
        assertThat(viewModel.getPosts().getValue(), notNullValue());
        verify(postsRepository).loadConversation("123");
    }

    @Test
    public void testRefresh() {
        assertThat(viewModel.getPosts().getValue(), nullValue());
        viewModel.getPosts().observeForever(mock(Observer.class));
        viewModel.setView(BaseListViewModel.TIMELINE, null);
        assertThat(viewModel.getPosts().getValue(), notNullValue());

        viewModel.refresh();
        assertThat(viewModel.getPosts().getValue(), notNullValue());
        verify(postsRepository, times(2)).loadTimeline();

        viewModel.setView(BaseListViewModel.MENTIONS, null);
        assertThat(viewModel.getPosts().getValue(), notNullValue());

        viewModel.refresh();
        assertThat(viewModel.getPosts().getValue(), notNullValue());
        verify(postsRepository, times(2)).loadMentions();

        viewModel.setView(BaseListViewModel.FAVORITES, null);
        assertThat(viewModel.getPosts().getValue(), notNullValue());

        viewModel.refresh();
        assertThat(viewModel.getPosts().getValue(), notNullValue());
        verify(postsRepository, times(2)).loadFavorites();

        viewModel.setView(BaseListViewModel.CONVERSATION, "123");
        assertThat(viewModel.getPosts().getValue(), notNullValue());

        viewModel.refresh();
        assertThat(viewModel.getPosts().getValue(), notNullValue());
        verify(postsRepository, times(2)).loadConversation("123");
    }
}
