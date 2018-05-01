package com.dialogapp.dialog.ui.profilescreen;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import com.dialogapp.dialog.model.Item;
import com.dialogapp.dialog.model.UserInfo;
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

    @Inject
    public ProfileViewModel(PostsRepository postsRepository) {
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
    }

    public LiveData<Resource<UserInfo>> getUserInfo() {
        return userInfo;
    }

    public LiveData<Resource<List<Item>>> getUserPosts() {
        return userPosts;
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
}
