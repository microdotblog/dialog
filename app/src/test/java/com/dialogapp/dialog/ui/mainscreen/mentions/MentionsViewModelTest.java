package com.dialogapp.dialog.ui.mainscreen.mentions;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;

import com.dialogapp.dialog.TestUtil;
import com.dialogapp.dialog.model.Item;
import com.dialogapp.dialog.model.Post;
import com.dialogapp.dialog.repository.PostsRepository;
import com.dialogapp.dialog.util.Resource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
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

        MutableLiveData<Resource<List<Post>>> data = new MutableLiveData<>();
        List<Item> mentionsItems = TestUtil.readFromJson(getClass().getClassLoader(), "mentions.json");
        List<Post> mentionsData = mentionsItems.stream().map(x -> new Post(x.getId(), x.getUrl(), x.getContentHtml(),
                x.getDatePublished(), x.getMicroblog().dateRelative, x.getMicroblog().isDeletable,
                x.getMicroblog().isFavorite, x.getAuthor().name, x.getAuthor().url, x.getAuthor().avatar,
                x.getAuthor().microblog.username)).collect(Collectors.toList());
        Resource<List<Post>> mentionsResource = Resource.success(mentionsData);
        data.setValue(mentionsResource);
        when(postsRepository.loadMentions()).thenReturn(data);

        viewModel = new MentionsViewModel(postsRepository);
    }

    @Test
    public void loadMentions() throws Exception {
        viewModel.getMentionsPosts().observeForever(mock(Observer.class));
        assertThat(viewModel.getMentionsPosts().getValue(), notNullValue());
    }
}
