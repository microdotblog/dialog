//package com.dialogapp.dialog.repository;
//
//import androidx.lifecycle.LiveData;
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//
//import com.dialogapp.dialog.AppExecutors;
//import com.dialogapp.dialog.api.ApiResponse;
//import com.dialogapp.dialog.api.MicroblogService;
//import com.dialogapp.dialog.db.AccountDao;
//import com.dialogapp.dialog.model.FollowingAccountInfo;
//import com.dialogapp.dialog.model.AccountResponse;
//import com.dialogapp.dialog.model.VerifiedAccount;
//import com.dialogapp.dialog.util.Resource;
//
//import java.util.List;
//
//import javax.inject.Inject;
//import javax.inject.Singleton;
//
//@Singleton
//public class AccountRepository {
//
//    private final AppExecutors appExecutors;
//    private final MicroblogService microblogService;
//    private final AccountDao accountDao;
//
//    @Inject
//    public AccountRepository(AppExecutors appExecutors, AccountDao accountDao, MicroblogService microblogService) {
//        this.appExecutors = appExecutors;
//        this.accountDao = accountDao;
//        this.microblogService = microblogService;
//    }
//
//    public LiveData<Resource<VerifiedAccount>> verifyToken(String token) {
//        return new NetworkBoundResource<VerifiedAccount, VerifiedAccount>(appExecutors) {
//            VerifiedAccount verifiedAccountData;
//
//            @Override
//            protected boolean shouldFetch(@Nullable VerifiedAccount dbData) {
//                // Always fetch
//                return true;
//            }
//
//            @NonNull
//            @Override
//            protected LiveData<ApiResponse<VerifiedAccount>> createCall() {
//                return microblogService.verifyToken(token);
//            }
//
//            @Override
//            protected void saveCallResult(@NonNull VerifiedAccount item) {
//                // No need to store this in DB
//                verifiedAccountData = item;
//            }
//
//            @NonNull
//            @Override
//            protected LiveData<VerifiedAccount> loadFromDb() {
//                return new LiveData<VerifiedAccount>() {
//                    @Override
//                    protected void onActive() {
//                        super.onActive();
//                        setValue(verifiedAccountData);
//                    }
//                };
//            }
//        }.asLiveData();
//    }
//
//    public LiveData<Resource<AccountResponse>> loadAccountData(String username) {
//        return new NetworkBoundResource<AccountResponse, AccountResponse>(appExecutors) {
//            @Override
//            protected boolean shouldFetch(@Nullable AccountResponse dbData) {
//                return true;
//            }
//
//            @NonNull
//            @Override
//            protected LiveData<ApiResponse<AccountResponse>> createCall() {
//                return microblogService.getAccountData();
//            }
//
//            @Override
//            protected void saveCallResult(@NonNull AccountResponse item) {
//                accountDao.insert(item);
//            }
//
//            @NonNull
//            @Override
//            protected LiveData<AccountResponse> loadFromDb() {
//                return accountDao.fetchAccountInfo(username);
//            }
//        }.asLiveData();
//    }
//
//    public LiveData<Resource<List<FollowingAccountInfo>>> loadFollowingData(String username) {
//        return new NetworkBoundResource<List<FollowingAccountInfo>, List<FollowingAccountInfo>>(appExecutors) {
//            List<FollowingAccountInfo> followingAccountInfo;
//
//            @Override
//            protected boolean shouldFetch(@Nullable List<FollowingAccountInfo> dbData) {
//                return followingAccountInfo == null;
//            }
//
//            @NonNull
//            @Override
//            protected LiveData<ApiResponse<List<FollowingAccountInfo>>> createCall() {
//                return microblogService.getFollowing(username);
//            }
//
//            @Override
//            protected void saveCallResult(@NonNull List<FollowingAccountInfo> item) {
//                followingAccountInfo = item;
//            }
//
//            @NonNull
//            @Override
//            protected LiveData<List<FollowingAccountInfo>> loadFromDb() {
//                return new LiveData<List<FollowingAccountInfo>>() {
//                    @Override
//                    protected void onActive() {
//                        super.onActive();
//                        setValue(followingAccountInfo);
//                    }
//                };
//            }
//        }.asLiveData();
//    }
//
//    public void removeAccountData() {
//        appExecutors.diskIO().execute(accountDao::dropAccount);
//    }
//}
