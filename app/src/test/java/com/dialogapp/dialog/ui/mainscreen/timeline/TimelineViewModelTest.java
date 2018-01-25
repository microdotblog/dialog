package com.dialogapp.dialog.ui.mainscreen.timeline;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;

import com.dialogapp.dialog.TestUtil;
import com.dialogapp.dialog.model.Item;
import com.dialogapp.dialog.repository.Endpoints;
import com.dialogapp.dialog.repository.PostsRepository;
import com.dialogapp.dialog.util.Resource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class TimelineViewModelTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private TimelineViewModel viewModel;
    private PostsRepository postsRepository;

    @Before
    public void setUp() {
        postsRepository = mock(PostsRepository.class);
        viewModel = new TimelineViewModel(postsRepository);
    }

    @Test
    public void loadTimeline() throws Exception {
        assertThat(viewModel.getTimelinePosts().getValue(), nullValue());
    }

    @Test
    public void refreshTimeline() {
        MutableLiveData<Resource<List<Item>>> data = new MutableLiveData<>();
        List<Item> timelineItems = TestUtil.createListOfEndpoint(Endpoints.TIMELINE);
        Resource<List<Item>> timelineResource = Resource.success(timelineItems);
        data.setValue(timelineResource);
        when(postsRepository.loadTimeline()).thenReturn(data);

        viewModel.getTimelinePosts().observeForever(mock(Observer.class));
        verify(postsRepository, never()).loadTimeline();
        viewModel.refresh();
        verify(postsRepository).loadTimeline();
        assertThat(viewModel.getTimelinePosts().getValue(), notNullValue());
    }

    @Test
    public void shouldNotRefreshTimeline() {
        MutableLiveData<Resource<List<Item>>> data = new MutableLiveData<>();
        List<Item> timelineItems = TestUtil.createListOfEndpoint(Endpoints.TIMELINE);
        Resource<List<Item>> timelineResource = Resource.success(timelineItems);
        data.setValue(timelineResource);
        when(postsRepository.loadTimeline()).thenReturn(data);

        viewModel.getTimelinePosts().observeForever(mock(Observer.class));
        verify(postsRepository, never()).loadTimeline();
        viewModel.refresh.setValue(false);
        verify(postsRepository, never()).loadTimeline();
        assertThat(viewModel.getTimelinePosts().getValue(), nullValue());
    }
}
