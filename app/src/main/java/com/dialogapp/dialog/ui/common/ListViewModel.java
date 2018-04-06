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
            switch (input) {
                case TIMELINE:
                    return postsRepository.loadTimeline(refresh);
                case MENTIONS:
                    return postsRepository.loadMentions(refresh);
                case FAVORITES:
                    return postsRepository.loadFavorites(refresh);
                case CONVERSATION:
                    return postsRepository.loadConversation(arg);
                case DISCOVER:
                    return postsRepository.loadDiscover(arg, refresh);
                default:
                    return AbsentLiveData.create();
            }
        });
    }
}
