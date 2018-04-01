package com.dialogapp.dialog.ui.base;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.IntDef;

import com.dialogapp.dialog.model.Item;
import com.dialogapp.dialog.util.Resource;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

/**
 * Base ViewModel class for list views
 */

public abstract class BaseListViewModel extends ViewModel {
    public static final int TIMELINE = 0;
    public static final int MENTIONS = 1;
    public static final int FAVORITES = 2;
    public static final int CONVERSATION = 3;

    protected MutableLiveData<Integer> view = new MutableLiveData<>();
    protected LiveData<Resource<List<Item>>> posts;
    protected String arg;

    public LiveData<Resource<List<Item>>> getPosts() {
        return posts;
    }

    public void setView(@ViewTypeDef Integer view, String arg) {
        this.arg = arg;
        this.view.setValue(view);
    }

    public void refresh() {
        this.view.setValue(this.view.getValue());
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TIMELINE, MENTIONS, FAVORITES, CONVERSATION})
    @interface ViewTypeDef {
    }
}
