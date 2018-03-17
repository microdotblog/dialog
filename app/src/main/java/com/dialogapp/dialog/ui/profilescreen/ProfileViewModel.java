package com.dialogapp.dialog.ui.profilescreen;

import android.arch.lifecycle.Transformations;

import com.dialogapp.dialog.repository.PostsRepository;
import com.dialogapp.dialog.ui.common.BaseListViewModel;
import com.dialogapp.dialog.util.AbsentLiveData;

import javax.inject.Inject;

public class ProfileViewModel extends BaseListViewModel {
    private String username;

    @Inject
    public ProfileViewModel(PostsRepository postsRepository) {
        posts = Transformations.switchMap(refresh, input -> {
            if (input) {
                return postsRepository.loadPostsByUsername(username);
            } else {
                return AbsentLiveData.create();
            }
        });
        refresh();
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
