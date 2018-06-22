package com.dialogapp.dialog.ui.profilescreen;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import com.dialogapp.dialog.model.AccountInfo;
import com.dialogapp.dialog.model.Item;
import com.dialogapp.dialog.repository.AccountRepository;
import com.dialogapp.dialog.repository.PostsRepository;
import com.dialogapp.dialog.util.AbsentLiveData;
import com.dialogapp.dialog.util.Objects;
import com.dialogapp.dialog.util.Resource;

import java.util.List;

import javax.inject.Inject;

public class ProfileViewModel extends ViewModel {
    private final MutableLiveData<String> user = new MutableLiveData<>();
    private final LiveData<Resource<UserInfo>> userInfo;
    private final LiveData<Resource<List<Item>>> userPosts;
    private final MutableLiveData<String> self = new MutableLiveData<>();
    private final LiveData<Resource<List<AccountInfo>>> followingData;

    @Inject
    public ProfileViewModel(PostsRepository postsRepository, AccountRepository accountRepository) {
        userInfo = Transformations.switchMap(user, input -> {
            if (!input.isEmpty()) {
                return postsRepository.loadUserData(input);
            } else {
                return AbsentLiveData.create();
            }
        });
        userPosts = Transformations.switchMap(user, input -> {
            if (!input.isEmpty()) {
                return postsRepository.loadPostsByUsername(input);
            } else {
                return AbsentLiveData.create();
            }
        });
        followingData = Transformations.switchMap(self, input -> {
            if (!input.isEmpty()) {
                return accountRepository.loadFollowingData(input);
            } else {
                return AbsentLiveData.create();
            }
        });
    }

    public LiveData<Resource<UserInfo>> getUserInfo() {
        return userInfo;
    }

    public LiveData<Resource<List<Item>>> getUserPosts() {
        return userPosts;
    }

    public LiveData<Resource<List<AccountInfo>>> getFollowingData() {
        return followingData;
    }

    public void refresh() {
        if (this.user.getValue() != null) {
            user.setValue(this.user.getValue());
        }
    }

    public void setUsername(String username) {
        if (Objects.equals(this.user.getValue(), username))
            return;

        this.user.setValue(username);
    }

    public void setFollowing(String username) {
        if (Objects.equals(this.self.getValue(), username))
            return;
        this.self.setValue(username);
    }

    public void refreshFollowingData() {
        this.self.setValue(this.self.getValue());
    }
}
