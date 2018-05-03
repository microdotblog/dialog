package com.dialogapp.dialog.ui.common;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import com.dialogapp.dialog.repository.PostRequestManager;
import com.dialogapp.dialog.util.Event;
import com.dialogapp.dialog.util.Resource;

import javax.inject.Inject;

public class RequestViewModel extends ViewModel {
    private final MutableLiveData<User> user = new MutableLiveData<>();
    private LiveData<Event<Resource<Boolean>>> responseFollow;

    private final MutableLiveData<Favorite> favorite = new MutableLiveData<>();
    private LiveData<Event<Resource<Boolean>>> responseFavorite;

    @Inject
    public RequestViewModel(PostRequestManager postRequestManager) {
        responseFollow = Transformations.switchMap(user,
                input -> postRequestManager.followUser(input.username, input.shouldFollow));

        responseFavorite = Transformations.switchMap(favorite,
                input -> postRequestManager.sendFavoriteRequest(input.id, input.shouldFavorite));
    }

    public void setFollowState(String username, boolean shouldFollow) {
        this.user.setValue(new User(username, shouldFollow));
    }

    public LiveData<Event<Resource<Boolean>>> getResponseFollow() {
        return responseFollow;
    }

    public void setFavoriteState(String id, boolean shouldFavorite) {
        this.favorite.setValue(new Favorite(id, shouldFavorite));
    }

    public LiveData<Event<Resource<Boolean>>> getResponseFavorite() {
        return responseFavorite;
    }

    public static class User {
        public final String username;
        public final boolean shouldFollow;

        public User(String username, boolean shouldFollow) {
            this.username = username;
            this.shouldFollow = shouldFollow;
        }
    }

    public static class Favorite {
        public final String id;
        public final boolean shouldFavorite;

        public Favorite(String id, boolean shouldFavorite) {
            this.id = id;
            this.shouldFavorite = shouldFavorite;
        }
    }
}
