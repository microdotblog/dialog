package com.dialogapp.dialog.ui.mainscreen;

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

import static com.dialogapp.dialog.ui.mainscreen.ListFragment.MENTIONS;
import static com.dialogapp.dialog.ui.mainscreen.ListFragment.TIMELINE;

public class ListViewModel extends ViewModel {
    protected MutableLiveData<Integer> view = new MutableLiveData<>();
    protected LiveData<Resource<List<Item>>> posts;

    @Inject
    public ListViewModel(PostsRepository postsRepository) {
        posts = Transformations.switchMap(view, input -> {
            switch (input) {
                case TIMELINE:
                    return postsRepository.loadTimeline();
                case MENTIONS:
                    return postsRepository.loadMentions();
                default:
                    return AbsentLiveData.create();
            }
        });
    }

    public LiveData<Resource<List<Item>>> getPosts() {
        return posts;
    }

    public void setView(Integer view) {
        if (Objects.equals(this.view.getValue(), view))
            return;
        this.view.setValue(view);
    }

    public void refresh() {
        if (view != null) {
            this.view.setValue(view.getValue());
        }
    }
}
