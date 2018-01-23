package com.dialogapp.dialog.repository;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;

import com.dialogapp.dialog.TestUtil;
import com.dialogapp.dialog.api.ApiResponse;
import com.dialogapp.dialog.api.MicroblogService;
import com.dialogapp.dialog.db.AccountDao;
import com.dialogapp.dialog.model.AccountResponse;
import com.dialogapp.dialog.model.VerifiedAccount;
import com.dialogapp.dialog.util.ApiUtil;
import com.dialogapp.dialog.util.InstantAppExecutors;
import com.dialogapp.dialog.util.Resource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class AccountRepositoryTest {
    private MicroblogService microblogService;
    private AccountRepository accountRepository;
    private AccountDao accountDao;

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() {
        accountDao = mock(AccountDao.class);
        microblogService = mock(MicroblogService.class);
        accountRepository = new AccountRepository(new InstantAppExecutors(), accountDao, microblogService);
    }

    @Test
    public void loadAccountData() {
        accountRepository.loadAccountData("abc");
        verify(accountDao).fetchAccountInfo("abc");
    }

    @Test
    public void loadVerifiedAccountData() {
        VerifiedAccount verifiedAccount = TestUtil.createUnverifiedAccount(false);
        LiveData<ApiResponse<VerifiedAccount>> call = ApiUtil.successCall(verifiedAccount);
        when(microblogService.verifyToken("token123")).thenReturn(call);

        Observer<Resource<VerifiedAccount>> observer = mock(Observer.class);
        accountRepository.verifyToken("token123").observeForever(observer);

        verify(microblogService).verifyToken("token123");
        verify(observer).onChanged(Resource.success(verifiedAccount));
    }

    @Test
    public void callNetworkToLoadAccount() {
        MutableLiveData<AccountResponse> dbData = new MutableLiveData<>();
        when(accountDao.fetchAccountInfo("abc123")).thenReturn(dbData);
        AccountResponse accountResponse = TestUtil.createAccount("abc123");
        LiveData<ApiResponse<AccountResponse>> call = ApiUtil.successCall(accountResponse);
        when(microblogService.getAccountData()).thenReturn(call);

        Observer<Resource<AccountResponse>> observer = mock(Observer.class);

        accountRepository.loadAccountData("abc123").observeForever(observer);
        verify(microblogService, never()).getAccountData();
        MutableLiveData<AccountResponse> updatedDbData = new MutableLiveData<>();
        when(accountDao.fetchAccountInfo("abc123")).thenReturn(updatedDbData);
        dbData.setValue(null); // so that it calls the network
        verify(microblogService).getAccountData();
    }

    @Test
    public void dontCallNetworkToLoadAccount() {
        MutableLiveData<AccountResponse> dbData = new MutableLiveData<>();
        AccountResponse account = TestUtil.createAccount("abc123");
        dbData.setValue(account);
        when(accountDao.fetchAccountInfo("abc123")).thenReturn(dbData);

        Observer<Resource<AccountResponse>> observer = mock(Observer.class);

        accountRepository.loadAccountData("abc123").observeForever(observer);
        verify(microblogService, never()).getAccountData();
        verify(observer).onChanged(Resource.success(account));
    }
}
