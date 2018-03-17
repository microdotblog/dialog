package com.dialogapp.dialog.ui.profilescreen;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;

import com.dialogapp.dialog.model.AccountResponse;
import com.dialogapp.dialog.repository.AccountRepository;
import com.dialogapp.dialog.repository.PostsRepository;
import com.dialogapp.dialog.util.Resource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
@RunWith(JUnit4.class)
public class ProfileViewModelTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private ProfileViewModel viewModel;
    private AccountViewModel accountViewModel;
    private PostsRepository postsRepository;
    private AccountRepository accountRepository;

    @Before
    public void setUp() throws IOException {
        postsRepository = mock(PostsRepository.class);
        accountRepository = mock(AccountRepository.class);
        viewModel = new ProfileViewModel(postsRepository);
        accountViewModel = new AccountViewModel(accountRepository);

        MutableLiveData<Resource<AccountResponse>> data = new MutableLiveData<>();
        AccountResponse accountResponse = new AccountResponse("foobar", "foo@xyz.com",
                "foo", "xyz.com", "blahblah", null, null,
                true, true);
        Resource<AccountResponse> mentionsResource = Resource.success(accountResponse);
        data.setValue(mentionsResource);
        when(accountRepository.loadAccountData("foobar")).thenReturn(data);
    }

    @Test
    public void loadAccountData() throws Exception {
        accountViewModel.getAccountData().observeForever(mock(Observer.class));

        assertThat(viewModel.getPosts().getValue(), nullValue());
        accountViewModel.setUsername("foobar");
        assertThat(accountViewModel.getAccountData().getValue(), notNullValue());
        assertThat(accountViewModel.getAccountData().getValue().data.getEmail(), is("foo@xyz.com"));
        assertThat(accountViewModel.getAccountData().getValue().data.getAboutMe(), is("blahblah"));
    }
}
