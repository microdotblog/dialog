package com.dialogapp.dialog.ui.mainscreen.mentions;

import android.arch.lifecycle.Transformations;

import com.dialogapp.dialog.repository.PostsRepository;
import com.dialogapp.dialog.ui.common.BaseListViewModel;

import javax.inject.Inject;

public class MentionsViewModel extends BaseListViewModel {

    @Inject
    public MentionsViewModel(PostsRepository postsRepository) {
        posts = Transformations.switchMap(refresh, postsRepository::loadMentions);
        this.refresh.setValue(false);
    }
}
