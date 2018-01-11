package com.dialogapp.dialog.repository;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;

import com.dialogapp.dialog.api.ApiResponse;
import com.dialogapp.dialog.api.MicroblogService;
import com.dialogapp.dialog.model.AccountResponse;
import com.dialogapp.dialog.util.ApiUtil;
import com.dialogapp.dialog.util.InstantAppExecutors;
import com.dialogapp.dialog.util.Resource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.dialogapp.dialog.TestUtil;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class AccountRepositoryTest {
    private MicroblogService microblogService;
    private AccountRepository accountRepository;

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() throws Exception {
        microblogService = mock(MicroblogService.class);
        accountRepository = new AccountRepository(new InstantAppExecutors(), microblogService);
    }

    @Test
    public void shouldCallNetwork() throws Exception {
        AccountResponse accountResponse = TestUtil.createAccount("abc123");
        LiveData<ApiResponse<AccountResponse>> call = ApiUtil.successCall(accountResponse);
        when(microblogService.getAccountData("abc123")).thenReturn(call);
        Observer<Resource<AccountResponse>> observer = mock(Observer.class);

        accountRepository.loadAccountData("abc123").observeForever(observer);
        verify(microblogService).getAccountData("abc123");
    }
}
