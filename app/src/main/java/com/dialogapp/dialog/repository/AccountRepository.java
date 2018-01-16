package com.dialogapp.dialog.repository;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dialogapp.dialog.AppExecutors;
import com.dialogapp.dialog.api.ApiResponse;
import com.dialogapp.dialog.api.MicroblogService;
import com.dialogapp.dialog.model.AccountResponse;
import com.dialogapp.dialog.model.VerifiedAccount;
import com.dialogapp.dialog.util.AbsentLiveData;
import com.dialogapp.dialog.util.CacheLiveData;
import com.dialogapp.dialog.util.Resource;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AccountRepository {

    private final AppExecutors appExecutors;
    private final MicroblogService microblogService;

    private VerifiedAccount verifiedAccountData;

    @Inject
    public AccountRepository(AppExecutors appExecutors, MicroblogService microblogService) {
        this.appExecutors = appExecutors;
        this.microblogService = microblogService;
    }

    public LiveData<Resource<VerifiedAccount>> verifyToken(String token) {
        return new NetworkBoundResource<VerifiedAccount, VerifiedAccount>(appExecutors) {
            @Override
            protected boolean shouldFetch(@Nullable VerifiedAccount dbData) {
                // Always fetch
                return true;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<VerifiedAccount>> createCall() {
                return microblogService.verifyToken(token);
            }

            @Override
            protected void saveCallResult(@NonNull VerifiedAccount item) {
                // No need to store this in DB
                verifiedAccountData = item;
            }

            @NonNull
            @Override
            protected LiveData<VerifiedAccount> loadFromDb() {
                return CacheLiveData.getAsLiveData(verifiedAccountData);
            }
        }.asLiveData();
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
