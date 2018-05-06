package com.dialogapp.dialog.ui.common;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import com.dialogapp.dialog.repository.PostRequestManager;
import com.dialogapp.dialog.util.Event;
import com.dialogapp.dialog.util.Objects;
import com.dialogapp.dialog.util.Resource;

import javax.inject.Inject;

public class RequestViewModel extends ViewModel {
    private final MutableLiveData<User> user = new MutableLiveData<>();
    private LiveData<Event<Resource<Boolean>>> responseFollow;

    private final MutableLiveData<Favorite> favorite = new MutableLiveData<>();
    private LiveData<Event<Resource<Boolean>>> responseFavorite;

    private final MutableLiveData<Reply> reply = new MutableLiveData<>();
    private LiveData<Event<Resource<Boolean>>> responseReply;

    @Inject
    public RequestViewModel(PostRequestManager postRequestManager) {
        responseFollow = Transformations.switchMap(user,
                input -> postRequestManager.followUser(input.username, input.shouldFollow));

        responseFavorite = Transformations.switchMap(favorite,
                input -> postRequestManager.sendFavoriteRequest(input.id, input.shouldFavorite));

        responseReply = Transformations.switchMap(reply, input -> postRequestManager.sendReply(input.id, input.text));
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

    public void sendReply(String id, String text) {
        Reply update = new Reply(id, text);
        if (Objects.equals(this.reply.getValue(), update))
            return;
        this.reply.setValue(new Reply(id, text));
    }

    public LiveData<Event<Resource<Boolean>>> getResponseReply() {
        return responseReply;
    }

    public void retryReply() {
        Reply current = this.reply.getValue();
        if (current != null && !current.isEmpty())
            this.reply.setValue(current);
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

    private class Reply {
        public final String id;
        public final String text;

        public Reply(String id, String text) {
            this.id = id;
            this.text = text;
        }

        boolean isEmpty() {
            return id == null || text == null || id.length() == 0 || text.length() == 0;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Reply reply = (Reply) o;

            if (id != null ? !id.equals(reply.id) : reply.id != null) {
                return false;
            }
            return text != null ? text.equals(reply.text) : reply.text == null;
        }

        @Override
        public int hashCode() {
            int result = id != null ? id.hashCode() : 0;
            result = 31 * result + (text != null ? text.hashCode() : 0);
            return result;
        }
    }
}
