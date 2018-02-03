package com.dialogapp.dialog.ui.mainscreen.mentions;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.dialogapp.dialog.model.Post;
import com.dialogapp.dialog.repository.PostsRepository;
import com.dialogapp.dialog.util.Resource;

import java.util.List;

import javax.inject.Inject;

public class MentionsViewModel extends ViewModel {
    private final LiveData<Resource<List<Post>>> mentionsPosts;

    @Inject
    public MentionsViewModel(PostsRepository postsRepository) {
        mentionsPosts = postsRepository.loadMentions();
    }

    public LiveData<Resource<List<Post>>> getMentionsPosts() {
        return mentionsPosts;
    }
}
