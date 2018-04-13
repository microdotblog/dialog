package com.dialogapp.dialog.ui.base;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.IntDef;

import com.dialogapp.dialog.model.Item;
import com.dialogapp.dialog.util.Objects;
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
    public static final int DISCOVER = 4;

    protected MutableLiveData<ViewState> view = new MutableLiveData<>();
    protected LiveData<Resource<List<Item>>> posts;

    public LiveData<Resource<List<Item>>> getPosts() {
        return posts;
    }

    public void setView(@ViewTypeDef Integer view, String arg) {
        ViewState update = new ViewState(view, arg);
        if (Objects.equals(this.view.getValue(), update))
            return;
        this.view.setValue(update);
    }

    public void refresh() {
        ViewState currentView = this.view.getValue();
        if (currentView != null && !currentView.isEmpty()) {
            this.view.setValue(currentView);
        }
    }

    public static class ViewState {
        public final Integer view;
        public final String arg;

        ViewState(Integer view, String arg) {
            this.view = view;
            this.arg = arg;
        }

        public boolean isEmpty() {
            return view == null;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }

            ViewState viewState = (ViewState) obj;

            if (view != null ? !view.equals(viewState.view) : viewState.view != null) {
                return false;
            }
            return arg != null ? arg.equals(viewState.arg) : viewState.arg == null;
        }

        @Override
        public int hashCode() {
            int result = view != null ? view.hashCode() : 0;
            result = 31 * result + (arg != null ? arg.hashCode() : 0);
            return result;
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TIMELINE, MENTIONS, FAVORITES, CONVERSATION, DISCOVER})
    @interface ViewTypeDef {
    }
}
