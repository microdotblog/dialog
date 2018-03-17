package com.dialogapp.dialog.ui.mainscreen.timeline;

import android.arch.lifecycle.Transformations;

import com.dialogapp.dialog.repository.PostsRepository;
import com.dialogapp.dialog.ui.common.BaseListViewModel;

import javax.inject.Inject;

public class TimelineViewModel extends BaseListViewModel {
    @Inject
    public TimelineViewModel(PostsRepository postsRepository) {
        posts = Transformations.switchMap(refresh, postsRepository::loadTimeline);
        this.refresh.setValue(false);
    }
}
