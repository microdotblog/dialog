package com.dialogapp.dialog.ui.loginscreen;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.annotation.VisibleForTesting;

import com.dialogapp.dialog.model.VerifiedAccount;
import com.dialogapp.dialog.repository.AccountRepository;
import com.dialogapp.dialog.repository.PostsRepository;
import com.dialogapp.dialog.util.Resource;

import javax.inject.Inject;

public class LoginViewModel extends ViewModel {

    @VisibleForTesting
    final MutableLiveData<String> token = new MutableLiveData<>();

    private final LiveData<Resource<VerifiedAccount>> account;
    private final AccountRepository accountRepository;
    private final PostsRepository postsRepository;

    @Inject
    public LoginViewModel(AccountRepository accountRepository, PostsRepository postsRepository) {
        account = Transformations.switchMap(token, accountRepository::verifyToken);
        this.accountRepository = accountRepository;
        this.postsRepository = postsRepository;
    }

    public void setToken(String token) {
        this.token.setValue(token);
    }

    public LiveData<Resource<VerifiedAccount>> verifyToken() {
        return account;
    }

    public void clearCachedData() {
        this.accountRepository.removeAccountData();
        this.postsRepository.removeUserData();
    }
}
