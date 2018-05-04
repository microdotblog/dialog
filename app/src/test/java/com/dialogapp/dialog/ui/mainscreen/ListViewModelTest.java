package com.dialogapp.dialog.ui.mainscreen;

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

import java.io.IOException;
import java.util.List;

import static com.dialogapp.dialog.ui.mainscreen.ListFragment.MENTIONS;
import static com.dialogapp.dialog.ui.mainscreen.ListFragment.TIMELINE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class ListViewModelTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private ListViewModel viewModel;
    private PostsRepository postsRepository;

    @Before
    public void setUp() {
        postsRepository = mock(PostsRepository.class);
        viewModel = new ListViewModel(postsRepository);
    }

    @Test
    public void testLoading() {
        viewModel.getPosts().observeForever(mock(Observer.class));

        viewModel.setView(TIMELINE);
        verify(postsRepository).loadTimeline();

        viewModel.setView(MENTIONS);
        verify(postsRepository).loadMentions();
    }

    @Test
    public void testRefresh() {
        viewModel.getPosts().observeForever(mock(Observer.class));
        verifyNoMoreInteractions(postsRepository);

        viewModel.setView(TIMELINE);
        viewModel.refresh();
        verify(postsRepository, times(2)).loadTimeline();

        viewModel.setView(MENTIONS);
        viewModel.refresh();
        verify(postsRepository, times(2)).loadMentions();
    }

    @Test
    public void sendResultToUI() throws IOException {
        MutableLiveData<Resource<List<Item>>> data = new MutableLiveData<>();
        MicroBlogResponse response = TestUtil.readFromJson(getClass().getClassLoader(), "response.json");
        Resource<List<Item>> listResource = Resource.success(response.items);
        when(postsRepository.loadMentions()).thenReturn(data);
        when(postsRepository.loadTimeline()).thenReturn(data);

        Observer<Resource<List<Item>>> observer = mock(Observer.class);
        viewModel.getPosts().observeForever(observer);
        viewModel.setView(TIMELINE);
        verify(observer, never()).onChanged(any(Resource.class));

        data.setValue(listResource);
        verify(observer).onChanged(listResource);
        reset(observer);

        data.setValue(listResource);
        viewModel.setView(MENTIONS);
        verify(observer).onChanged(listResource);
        reset(observer);
    }
}
