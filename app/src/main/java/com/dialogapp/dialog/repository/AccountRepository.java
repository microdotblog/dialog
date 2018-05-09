package com.dialogapp.dialog.repository;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dialogapp.dialog.AppExecutors;
import com.dialogapp.dialog.api.ApiResponse;
import com.dialogapp.dialog.api.MicroblogService;
import com.dialogapp.dialog.db.AccountDao;
import com.dialogapp.dialog.model.AccountInfo;
import com.dialogapp.dialog.model.AccountResponse;
import com.dialogapp.dialog.model.VerifiedAccount;
import com.dialogapp.dialog.util.Resource;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AccountRepository {

    private final AppExecutors appExecutors;
    private final MicroblogService microblogService;
    private final AccountDao accountDao;

    @Inject
    public AccountRepository(AppExecutors appExecutors, AccountDao accountDao, MicroblogService microblogService) {
        this.appExecutors = appExecutors;
        this.accountDao = accountDao;
        this.microblogService = microblogService;
    }

    public LiveData<Resource<VerifiedAccount>> verifyToken(String token) {
        return new NetworkBoundResource<VerifiedAccount, VerifiedAccount>(appExecutors) {
            VerifiedAccount verifiedAccountData;

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
                return new LiveData<VerifiedAccount>() {
                    @Override
                    protected void onActive() {
                        super.onActive();
                        setValue(verifiedAccountData);
                    }
                };
            }
        }.asLiveData();
    }

    public LiveData<Resource<AccountResponse>> loadAccountData(String username) {
        return new NetworkBoundResource<AccountResponse, AccountResponse>(appExecutors) {
            @Override
            protected boolean shouldFetch(@Nullable AccountResponse dbData) {
                return true;
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

    public LiveData<Resource<List<AccountInfo>>> loadFollowingData(String username) {
        return new NetworkBoundResource<List<AccountInfo>, List<AccountInfo>>(appExecutors) {
            List<AccountInfo> accountInfos;

            @Override
            protected boolean shouldFetch(@Nullable List<AccountInfo> dbData) {
                return accountInfos == null;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<AccountInfo>>> createCall() {
                return microblogService.getFollowing(username);
            }

            @Override
            protected void saveCallResult(@NonNull List<AccountInfo> item) {
                accountInfos = item;
            }

            @NonNull
            @Override
            protected LiveData<List<AccountInfo>> loadFromDb() {
                return new LiveData<List<AccountInfo>>() {
                    @Override
                    protected void onActive() {
                        super.onActive();
                        setValue(accountInfos);
                    }
                };
            }
        }.asLiveData();
    }

    public void removeAccountData() {
        appExecutors.diskIO().execute(accountDao::dropAccount);
    }
}
