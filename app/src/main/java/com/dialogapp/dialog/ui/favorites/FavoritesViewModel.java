package com.dialogapp.dialog.ui.favorites;

import android.arch.lifecycle.Transformations;

import com.dialogapp.dialog.repository.PostsRepository;
import com.dialogapp.dialog.ui.common.BaseListViewModel;
import com.dialogapp.dialog.util.AbsentLiveData;

import javax.inject.Inject;

public class FavoritesViewModel extends BaseListViewModel {
    @Inject
    public FavoritesViewModel(PostsRepository postsRepository) {
        posts = Transformations.switchMap(refresh, input -> {
            if (input) {
                return postsRepository.loadFavorites(input);
            } else {
                return AbsentLiveData.create();
            }
        });
        refresh();
    }
}
