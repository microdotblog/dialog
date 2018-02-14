package com.dialogapp.dialog.ui.mainscreen.mentions;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;

import com.dialogapp.dialog.TestUtil;
import com.dialogapp.dialog.model.Item;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        List<Item> mentionsItems = TestUtil.readFromJson(getClass().getClassLoader(), "mentions.json");
        Resource<List<Item>> mentionsResource = Resource.success(mentionsItems);
        data.setValue(mentionsResource);
        when(postsRepository.loadMentions(true)).thenReturn(data);

        viewModel = new MentionsViewModel(postsRepository);
    }

    @Test
    public void initialLoadIsNull() throws Exception {
        assertThat(viewModel.getPosts().getValue(), nullValue());
    }

    @Test
    public void refreshMentions() {
        viewModel.getPosts().observeForever(mock(Observer.class));
        verify(postsRepository, never()).loadMentions(true);
        viewModel.refresh();
        verify(postsRepository).loadMentions(true);
        assertThat(viewModel.getPosts().getValue(), notNullValue());
    }
}
