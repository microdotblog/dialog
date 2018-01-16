package com.dialogapp.dialog.ui.loginscreen;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;

import com.dialogapp.dialog.TestUtil;
import com.dialogapp.dialog.model.VerifiedAccount;
import com.dialogapp.dialog.repository.AccountRepository;
import com.dialogapp.dialog.util.Resource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
@RunWith(JUnit4.class)
public class LoginViewModelTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private LoginViewModel loginViewModel;
    private AccountRepository accountRepository;

    @Before
    public void setUp() {
        accountRepository = mock(AccountRepository.class);
        loginViewModel = new LoginViewModel(accountRepository);
    }

    @Test
    public void testLoginCall() throws Exception {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        loginViewModel.verifyToken().observeForever(mock(Observer.class));

        loginViewModel.setToken("VALIDTOKEN");
        verify(accountRepository).verifyToken(captor.capture());
        assertThat(captor.getValue(), is("VALIDTOKEN"));

        reset(accountRepository);

        loginViewModel.setToken("INVALIDTOKEN");
        verify(accountRepository).verifyToken(captor.capture());
        assertThat(captor.getValue(), is("INVALIDTOKEN"));
    }

    @Test
    public void sameToken_shouldNotRefresh() {
        Observer<String> observer = mock(Observer.class);
        loginViewModel.token.observeForever(observer);
        verifyNoMoreInteractions(observer);

        loginViewModel.setToken("VALIDTOKEN1");
        verify(observer).onChanged("VALIDTOKEN1");

        reset(observer);

        loginViewModel.setToken("VALIDTOKEN1");
        verifyNoMoreInteractions(observer);

        loginViewModel.setToken("VALIDTOKEN2");
        verify(observer).onChanged("VALIDTOKEN2");
    }

    @Test
    public void sendResultToUI() {
        MutableLiveData<Resource<VerifiedAccount>> foo = new MutableLiveData<>();
        MutableLiveData<Resource<VerifiedAccount>> bar = new MutableLiveData<>();

        when(accountRepository.verifyToken("foo")).thenReturn(foo);
        when(accountRepository.verifyToken("bar")).thenReturn(bar);

        Observer<Resource<VerifiedAccount>> observer = mock(Observer.class);
        loginViewModel.verifyToken().observeForever(observer);

        loginViewModel.setToken("foo");
        verify(observer, never()).onChanged(any(Resource.class));

        VerifiedAccount fooAccount = TestUtil.createUnverifiedAccount(true);
        Resource<VerifiedAccount> fooResource = Resource.success(fooAccount);

        foo.setValue(fooResource);
        verify(observer).onChanged(fooResource);

        reset(observer);

        loginViewModel.setToken("bar");
        verify(observer, never()).onChanged(any(Resource.class));

        VerifiedAccount barAccount = TestUtil.createUnverifiedAccount(false);
        Resource<VerifiedAccount> barResource = Resource.success(barAccount);

        bar.setValue(barResource);
        verify(observer).onChanged(barResource);
    }
}
