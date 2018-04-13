package com.dialogapp.dialog.ui.common;

import android.arch.lifecycle.Transformations;

import com.dialogapp.dialog.repository.PostsRepository;
import com.dialogapp.dialog.ui.base.BaseListViewModel;
import com.dialogapp.dialog.util.AbsentLiveData;

import javax.inject.Inject;

public class ListViewModel extends BaseListViewModel {
    @Inject
    public ListViewModel(PostsRepository postsRepository) {
        posts = Transformations.switchMap(view, input -> {
            switch (input.view) {
                case TIMELINE:
                    return postsRepository.loadTimeline();
                case MENTIONS:
                    return postsRepository.loadMentions();
                case FAVORITES:
                    return postsRepository.loadFavorites();
                case CONVERSATION:
                    return postsRepository.loadConversation(input.arg);
                case DISCOVER:
                    return postsRepository.loadDiscover(input.arg);
                default:
                    return AbsentLiveData.create();
            }
        });
    }
}
