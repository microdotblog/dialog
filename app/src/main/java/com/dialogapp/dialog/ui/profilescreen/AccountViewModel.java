package com.dialogapp.dialog.ui.profilescreen;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import com.dialogapp.dialog.model.AccountResponse;
import com.dialogapp.dialog.repository.AccountRepository;
import com.dialogapp.dialog.util.AbsentLiveData;
import com.dialogapp.dialog.util.Resource;

import javax.inject.Inject;

public class AccountViewModel extends ViewModel {
    protected final MutableLiveData<String> username = new MutableLiveData<>();

    protected LiveData<Resource<AccountResponse>> accountData;

    @Inject
    public AccountViewModel(AccountRepository accountRepository) {
        accountData = Transformations.switchMap(username, input -> {
            if (input != null)
                return accountRepository.loadAccountData(input);
            else
                return AbsentLiveData.create();
        });
    }

    public void setUsername(String username) {
        this.username.setValue(username);
    }

    public LiveData<Resource<AccountResponse>> getAccountData() {
        return accountData;
    }
}
