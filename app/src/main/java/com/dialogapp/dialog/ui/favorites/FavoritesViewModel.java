package com.dialogapp.dialog.ui.favorites;

import android.arch.lifecycle.Transformations;

import com.dialogapp.dialog.repository.PostsRepository;
import com.dialogapp.dialog.ui.common.BaseListViewModel;

import javax.inject.Inject;

public class FavoritesViewModel extends BaseListViewModel {
    @Inject
    public FavoritesViewModel(PostsRepository postsRepository) {
        posts = Transformations.switchMap(refresh, postsRepository::loadFavorites);
        this.refresh.setValue(false);
    }
}
