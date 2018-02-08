package com.dialogapp.dialog.ui.mainscreen.mentions;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import com.dialogapp.dialog.model.Item;
import com.dialogapp.dialog.repository.PostsRepository;
import com.dialogapp.dialog.util.AbsentLiveData;
import com.dialogapp.dialog.util.Resource;

import java.util.List;

import javax.inject.Inject;

public class MentionsViewModel extends ViewModel {

    private final MutableLiveData<Boolean> refresh;

    private final LiveData<Resource<List<Item>>> mentionsPosts;

    @Inject
    public MentionsViewModel(PostsRepository postsRepository) {
        refresh = new MutableLiveData<>();
        mentionsPosts = Transformations.switchMap(refresh, input -> {
            if (input)
                return postsRepository.loadMentions(input);
            else
                return AbsentLiveData.create();
        });
    }

    public LiveData<Resource<List<Item>>> getMentionsPosts() {
        return mentionsPosts;
    }

    public void refresh() {
        refresh.setValue(true);
    }
}
