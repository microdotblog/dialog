package com.dialogapp.dialog.ui.loginscreen;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.VisibleForTesting;

import com.dialogapp.dialog.model.AccountResponse;
import com.dialogapp.dialog.repository.AccountRepository;
import com.dialogapp.dialog.util.Resource;

import java.util.Objects;

import javax.inject.Inject;

public class LoginViewModel extends ViewModel {

    @VisibleForTesting
    final MutableLiveData<String> token = new MutableLiveData<>();

    private final LiveData<Resource<AccountResponse>> account;

    @Inject
    public LoginViewModel(AccountRepository accountRepository) {
        account = Transformations.switchMap(token, accountRepository::loadAccountData);
    }

    public void setToken(String token) {
        if (!Objects.equals(this.token.getValue(), token))
            this.token.setValue(token);
    }

    public LiveData<Resource<AccountResponse>> getAccount() {
        return account;
    }
}
