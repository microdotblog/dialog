package com.dialogapp.dialog.ui.mainscreen;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import com.dialogapp.dialog.model.AccountResponse;
import com.dialogapp.dialog.repository.AccountRepository;
import com.dialogapp.dialog.util.Resource;

import javax.inject.Inject;

public class MainViewModel extends ViewModel {

    final MutableLiveData<AccountInfo> accountInfo = new MutableLiveData<>();

    private final LiveData<Resource<AccountResponse>> account;

    @Inject
    public MainViewModel(AccountRepository accountRepository) {
        this.account = Transformations.switchMap(accountInfo,
                input -> accountRepository.loadAccountData(input.token, input.username));
    }

    public LiveData<Resource<AccountResponse>> getAccountData() {
        return account;
    }

    public void setAccountInfo(String token, String username) {
        AccountInfo update = new AccountInfo(token, username);
        accountInfo.setValue(update);
    }

    public void retry() {
        AccountInfo current = accountInfo.getValue();
        accountInfo.setValue(current);
    }

    static class AccountInfo {
        private final String token;
        private final String username;

        public AccountInfo(String token, String username) {
            this.token = token;
            this.username = username;
        }
    }
}
