package com.dialogapp.dialog.ui.profilescreen;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;

import com.dialogapp.dialog.TestUtil;
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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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
        ArgumentCaptor<Boolean> captor = ArgumentCaptor.forClass(Boolean.class);
        ArgumentCaptor<String> captor2 = ArgumentCaptor.forClass(String.class);
        viewModel.getUserData().observeForever(mock(Observer.class));

        viewModel.setUsername("dialog", false);
        verify(postsRepository).loadPostsByUsername(captor2.capture(), captor.capture());
        assertThat(captor.getValue(), is(false));
        assertThat(captor2.getValue(), is("dialog"));
    }

    @Test
    public void testRefresh() {
        ArgumentCaptor<Boolean> captor = ArgumentCaptor.forClass(Boolean.class);
        ArgumentCaptor<String> captor2 = ArgumentCaptor.forClass(String.class);
        viewModel.getUserData().observeForever(mock(Observer.class));

        viewModel.setUsername("dialog", true);
        verify(postsRepository).loadPostsByUsername(captor2.capture(), captor.capture());
        assertThat(captor.getValue(), is(true));
        assertThat(captor2.getValue(), is("dialog"));
    }

    @Test
    public void sendResultToUI() throws IOException {
        MutableLiveData<Resource<MicroBlogResponse>> data = new MutableLiveData<>();
        MicroBlogResponse response = TestUtil.readFromJson(getClass().getClassLoader(), "userpostsresponse.json");
        Resource<MicroBlogResponse> listResource = Resource.success(response);
        when(postsRepository.loadPostsByUsername("dialog", false)).thenReturn(data);

        Observer<Resource<MicroBlogResponse>> observer = mock(Observer.class);
        viewModel.getUserData().observeForever(observer);
        viewModel.setUsername("dialog", false);
        verify(observer, never()).onChanged(any(Resource.class));

        data.setValue(listResource);
        verify(observer).onChanged(listResource);
    }
}
