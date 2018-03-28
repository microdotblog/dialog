package com.dialogapp.dialog.ui.profilescreen;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import com.dialogapp.dialog.model.MicroBlogResponse;
import com.dialogapp.dialog.repository.PostsRepository;
import com.dialogapp.dialog.util.AbsentLiveData;
import com.dialogapp.dialog.util.Resource;

import javax.inject.Inject;

public class ProfileViewModel extends ViewModel {
    private final MutableLiveData<String> username = new MutableLiveData<>();
    private LiveData<Resource<MicroBlogResponse>> userData;

    @Inject
    public ProfileViewModel(PostsRepository postsRepository) {
        userData = Transformations.switchMap(username, input -> {
            if (!input.isEmpty()) {
                return postsRepository.loadPostsByUsername(input);
            } else {
                return AbsentLiveData.create();
            }
        });
    }

    public LiveData<Resource<MicroBlogResponse>> getUserData() {
        return userData;
    }

    public void setUsername(String username) {
        this.username.setValue(username);
    }
}
