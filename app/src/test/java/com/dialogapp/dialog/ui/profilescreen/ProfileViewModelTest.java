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

import java.io.IOException;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
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
    public void setUp() throws IOException {
        postsRepository = mock(PostsRepository.class);
        viewModel = new ProfileViewModel(postsRepository);

        MutableLiveData<Resource<MicroBlogResponse>> data = new MutableLiveData<>();
        MicroBlogResponse response = TestUtil.readFromJson(getClass().getClassLoader(), "userpostsresponse.json");
        Resource<MicroBlogResponse> microBlogResponseResource = Resource.success(response);
        data.setValue(microBlogResponseResource);
        when(postsRepository.loadPostsByUsername("dialog")).thenReturn(data);
    }

    @Test
    public void loadUserPosts() throws Exception {
        viewModel.getUserData().observeForever(mock(Observer.class));

        assertThat(viewModel.getUserData().getValue(), nullValue());
        viewModel.setUsername("dialog");
        verify(postsRepository).loadPostsByUsername("dialog");
        assertThat(viewModel.getUserData().getValue(), notNullValue());
    }
}
