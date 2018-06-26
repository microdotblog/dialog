package com.dialogapp.dialog.ui.mainscreen;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.dialogapp.dialog.model.Item;
import com.dialogapp.dialog.ui.base.BaseListFragment;
import com.dialogapp.dialog.util.Resource;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

public class ListFragment extends BaseListFragment {
    public static final int TIMELINE = 0;
    public static final int MENTIONS = 1;

    private static final String EXTRA_FRAGMENT = ListFragment.class.getName() + ".EXTRA_FRAGMENT";

    private final Observer<Resource<List<Item>>> observer = listResource -> {
        if (listResource != null) {
            setData(listResource.status, listResource.data, listResource.message);
        }
    };

    private int fragment;
    private ListViewModel viewModel;

    public static Fragment newInstance(@FragmentTypeDef int fragment) {
        ListFragment listFragment = new ListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_FRAGMENT, fragment);
        listFragment.setArguments(bundle);
        return listFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            fragment = getArguments().getInt(EXTRA_FRAGMENT);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = ViewModelProviders.of(this, getViewModelFactory()).get(ListViewModel.class);
        viewModel.setView(fragment);
        viewModel.getPosts().removeObserver(observer);
        viewModel.getPosts().observe(this, observer);
    }

    @Override
    protected void onRefresh() {
        viewModel.refresh();
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TIMELINE, MENTIONS})
    @interface FragmentTypeDef {
    }
}
