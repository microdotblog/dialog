package com.dialogapp.dialog.ui.mainscreen.mentions;

import android.arch.lifecycle.Transformations;

import com.dialogapp.dialog.repository.PostsRepository;
import com.dialogapp.dialog.ui.common.BaseListViewModel;
import com.dialogapp.dialog.util.AbsentLiveData;

import javax.inject.Inject;

public class MentionsViewModel extends BaseListViewModel {

    @Inject
    public MentionsViewModel(PostsRepository postsRepository) {
        posts = Transformations.switchMap(refresh, input -> {
            if (input)
                return postsRepository.loadMentions(input);
            else
                return AbsentLiveData.create();
        });
        refresh();
    }
}
