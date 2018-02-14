package com.dialogapp.dialog.ui.common;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.dialogapp.dialog.model.Item;
import com.dialogapp.dialog.util.Resource;

import java.util.List;

/**
 * Base ViewModel class for list views
 */

public class BaseListViewModel extends ViewModel {
    protected final MutableLiveData<Boolean> refresh = new MutableLiveData<>();

    protected LiveData<Resource<List<Item>>> posts;

    public void refresh() {
        refresh.setValue(true);
    }

    public LiveData<Resource<List<Item>>> getPosts() {
        return posts;
    }
}
