package com.dialogapp.dialog.ui.profilescreen.favorites;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import com.dialogapp.dialog.model.Item;
import com.dialogapp.dialog.repository.PostsRepository;
import com.dialogapp.dialog.util.AbsentLiveData;
import com.dialogapp.dialog.util.Objects;
import com.dialogapp.dialog.util.Resource;

import java.util.List;

import javax.inject.Inject;

public class FavoritesViewModel extends ViewModel {
    protected MutableLiveData<String> refresh = new MutableLiveData<>();
    protected LiveData<Resource<List<Item>>> posts;

    @Inject
    public FavoritesViewModel(PostsRepository postsRepository) {
        posts = Transformations.switchMap(refresh, input -> {
            if (input != null)
                return postsRepository.loadFavorites();
            else
                return AbsentLiveData.create();
        });
    }

    public LiveData<Resource<List<Item>>> getPosts() {
        return posts;
    }

    public void initView() {
        if (Objects.equals(refresh.getValue(), ""))
            return;
        refresh.setValue("");
    }

    public void refresh() {
        refresh.setValue("");
    }
}
