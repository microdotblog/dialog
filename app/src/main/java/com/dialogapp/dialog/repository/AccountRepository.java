package com.dialogapp.dialog.repository;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.dialogapp.dialog.AppExecutors;
import com.dialogapp.dialog.api.ApiResponse;
import com.dialogapp.dialog.api.MicroblogService;
import com.dialogapp.dialog.db.AccountDao;
import com.dialogapp.dialog.model.AccountResponse;
import com.dialogapp.dialog.model.VerifiedAccount;
import com.dialogapp.dialog.util.CacheLiveData;
import com.dialogapp.dialog.util.Resource;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AccountRepository {

    private final AppExecutors appExecutors;
    private final MicroblogService microblogService;
    private final AccountDao accountDao;

    @VisibleForTesting
    VerifiedAccount verifiedAccountData;

    @Inject
    public AccountRepository(AppExecutors appExecutors, AccountDao accountDao, MicroblogService microblogService) {
        this.appExecutors = appExecutors;
        this.accountDao = accountDao;
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

    public LiveData<Resource<AccountResponse>> loadAccountData(String username) {
        return new NetworkBoundResource<AccountResponse, AccountResponse>(appExecutors) {
            @Override
            protected boolean shouldFetch(@Nullable AccountResponse dbData) {
                return dbData == null;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<AccountResponse>> createCall() {
                return microblogService.getAccountData();
            }

            @Override
            protected void saveCallResult(@NonNull AccountResponse item) {
                accountDao.insert(item);
            }

            @NonNull
            @Override
            protected LiveData<AccountResponse> loadFromDb() {
                return accountDao.fetchAccountInfo(username);
            }
        }.asLiveData();
    }
}
