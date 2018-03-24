package com.dialogapp.dialog.ui.mainscreen.mentions;

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

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
@RunWith(JUnit4.class)
public class MentionsViewModelTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private MentionsViewModel viewModel;
    private PostsRepository postsRepository;

    @Before
    public void setUp() throws IOException {
        postsRepository = mock(PostsRepository.class);

        MutableLiveData<Resource<List<Item>>> data = new MutableLiveData<>();
        MicroBlogResponse response = TestUtil.readFromJson(getClass().getClassLoader(), "response.json");
        Resource<List<Item>> mentionsResource = Resource.success(response.items);
        data.setValue(mentionsResource);
        when(postsRepository.loadMentions(true)).thenReturn(data);

        viewModel = new MentionsViewModel(postsRepository);
    }

    @Test
    public void refreshMentions() {
        assertThat(viewModel.getPosts().getValue(), nullValue());
        viewModel.getPosts().observeForever(mock(Observer.class));

        // initial load
        assertThat(viewModel.getPosts().getValue(), notNullValue());

        // on refresh
        viewModel.refresh();
        verify(postsRepository, times(2)).loadMentions(true);
        assertThat(viewModel.getPosts().getValue(), notNullValue());
    }
}
