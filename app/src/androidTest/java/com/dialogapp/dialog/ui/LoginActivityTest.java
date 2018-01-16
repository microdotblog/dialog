package com.dialogapp.dialog.ui;

import android.arch.lifecycle.MutableLiveData;
import android.support.test.runner.AndroidJUnit4;

import com.dialogapp.dialog.model.AccountResponse;
import com.dialogapp.dialog.model.VerifiedAccount;
import com.dialogapp.dialog.ui.loginscreen.LoginViewModel;
import com.dialogapp.dialog.util.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    private LoginViewModel loginViewModel;
    private MutableLiveData<Resource<VerifiedAccount>> verifiedAccountData = new MutableLiveData<>();

    @Before
    public void setUp() throws Exception {
        loginViewModel = mock(LoginViewModel.class);
        when(loginViewModel.verifyToken()).thenReturn(verifiedAccountData);
    }

    @Test
    public void checkInput_invalidToken() throws Exception {

    }

    @Test
    public void checkInput_validToken() throws Exception {

    }
}
