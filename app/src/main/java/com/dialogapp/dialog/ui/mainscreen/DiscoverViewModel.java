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

public class DiscoverViewModel extends ViewModel {
    protected MutableLiveData<String> topic = new MutableLiveData<>();
    protected LiveData<Resource<List<Item>>> posts;

    @Inject
    public DiscoverViewModel(PostsRepository postsRepository) {
        posts = Transformations.switchMap(topic, input -> {
            if (input != null)
                return postsRepository.loadDiscover(input);
            else
                return AbsentLiveData.create();
        });
    }

    public LiveData<Resource<List<Item>>> getPosts() {
        return posts;
    }

    public void setTopic(String topic) {
        if (Objects.equals(this.topic.getValue(), topic))
            return;
        this.topic.setValue(topic);
    }

    public void refresh() {
        if (topic.getValue() != null) {
            topic.setValue(topic.getValue());
        }
    }
}
