package com.dialogapp.dialog.ui.mainscreen.timeline;

import android.arch.lifecycle.Transformations;

import com.dialogapp.dialog.repository.PostsRepository;
import com.dialogapp.dialog.ui.mainscreen.common.BaseListViewModel;
import com.dialogapp.dialog.util.AbsentLiveData;

import javax.inject.Inject;

public class TimelineViewModel extends BaseListViewModel {
    @Inject
    public TimelineViewModel(PostsRepository postsRepository) {
        posts = Transformations.switchMap(refresh, input -> {
            if (input)
                return postsRepository.loadTimeline(input);
            else
                return AbsentLiveData.create();
        });
    }
}
