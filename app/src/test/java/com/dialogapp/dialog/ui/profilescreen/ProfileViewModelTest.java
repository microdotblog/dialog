package com.dialogapp.dialog.ui.profilescreen;

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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
@RunWith(JUnit4.class)
public class ProfileViewModelTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private ProfileViewModel viewModel;
    private PostsRepository postsRepository;

    @Before
    public void setUp() {
        postsRepository = mock(PostsRepository.class);
        viewModel = new ProfileViewModel(postsRepository);
    }

    @Test
    public void loadUserPosts() {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> captor2 = ArgumentCaptor.forClass(String.class);
        viewModel.getUserInfo().observeForever(mock(Observer.class));
        viewModel.getUserPosts().observeForever(mock(Observer.class));
        verifyNoMoreInteractions(postsRepository);

        viewModel.setUsername("dialog");
        verify(postsRepository).loadPostsByUsername(captor.capture());
        verify(postsRepository).loadUserData(captor2.capture());
        assertThat(captor.getValue(), is("dialog"));
        assertThat(captor2.getValue(), is("dialog"));
    }

    @Test
    public void testRefresh() {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> captor2 = ArgumentCaptor.forClass(String.class);
        viewModel.getUserInfo().observeForever(mock(Observer.class));
        viewModel.getUserPosts().observeForever(mock(Observer.class));

        viewModel.setUsername("dialog");
        viewModel.refresh();
        verify(postsRepository, times(2)).loadUserData(captor.capture());
        verify(postsRepository, times(2)).loadPostsByUsername(captor2.capture());
        assertThat(captor.getValue(), is("dialog"));
        assertThat(captor2.getValue(), is("dialog"));
    }

    @Test
    public void sendResultToUI() throws IOException {
        MutableLiveData<Resource<UserInfo>> userDbData = new MutableLiveData<>();
        MutableLiveData<Resource<List<Item>>> userPosts = new MutableLiveData<>();
        MicroBlogResponse response = TestUtil.readFromJson(getClass().getClassLoader(), "userpostsresponse.json");
        UserInfo userInfo = new UserInfo(response.microblog.bio, response.author.name,
                response.author.url, response.author.avatar, response.microblog.is_following,
                response.microblog.is_you, response.microblog.following_count);
        Resource<UserInfo> userDataResource = Resource.success(userInfo);
        Resource<List<Item>> listResource = Resource.success(response.items);
        when(postsRepository.loadUserData("dialog")).thenReturn(userDbData);
        when(postsRepository.loadPostsByUsername("dialog")).thenReturn(userPosts);

        Observer<Resource<UserInfo>> observer = mock(Observer.class);
        Observer<Resource<List<Item>>> observer2 = mock(Observer.class);
        viewModel.getUserInfo().observeForever(observer);
        viewModel.getUserPosts().observeForever(observer2);
        verifyNoMoreInteractions(postsRepository);
        viewModel.setUsername("dialog");
        verify(observer, never()).onChanged(any(Resource.class));

        userDbData.setValue(userDataResource);
        userPosts.setValue(listResource);
        verify(observer).onChanged(userDataResource);
        verify(observer2).onChanged(listResource);
    }
}
