package com.dialogapp.dialog.ui.conversation;

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

public class ConversationViewModel extends ViewModel {
    protected MutableLiveData<String> postId = new MutableLiveData<>();
    protected LiveData<Resource<List<Item>>> posts;

    @Inject
    public ConversationViewModel(PostsRepository postsRepository) {
        posts = Transformations.switchMap(postId, input -> {
            if (input != null && !input.isEmpty())
                return postsRepository.loadConversation(input);
            else
                return AbsentLiveData.create();
        });
    }

    public LiveData<Resource<List<Item>>> getPosts() {
        return posts;
    }

    public void setPostId(String postId) {
        if (Objects.equals(this.postId.getValue(), postId))
            return;
        this.postId.setValue(postId);
    }

    public void refresh() {
        if (postId.getValue() != null) {
            postId.setValue(postId.getValue());
        }
    }
}
