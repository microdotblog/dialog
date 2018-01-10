package com.dialogapp.dialog.repository;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.dialogapp.dialog.AppExecutors;
import com.dialogapp.dialog.api.ApiResponse;
import com.dialogapp.dialog.api.MicroblogService;
import com.dialogapp.dialog.model.AccountResponse;
import com.dialogapp.dialog.util.AbsentLiveData;
import com.dialogapp.dialog.util.Resource;

public class AccountRepository {

    private final AppExecutors appExecutors;
    private final MicroblogService microblogService;

    public AccountRepository(AppExecutors appExecutors, MicroblogService microblogService) {
        this.appExecutors = appExecutors;
        this.microblogService = microblogService;
    }

    public static boolean isValid(String token) {
        if (token.length() < 20)
            return false;
        else
            return true;
    }

    public LiveData<Resource<AccountResponse>> loadAccountData(String token) {
        return new NetworkBoundResource<AccountResponse, AccountResponse>(appExecutors) {
            @Override
            protected boolean shouldFetch(AccountResponse dbData) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<AccountResponse>> createCall() {
                return microblogService.getAccountData(token);
            }

            @Override
            protected void saveCallResult(@NonNull AccountResponse item) {

            }

            @NonNull
            @Override
            protected LiveData<AccountResponse> loadFromDb() {
                return AbsentLiveData.create();
            }
        }.asLiveData();
    }
}
