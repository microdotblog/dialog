package com.dialogapp.dialog.ui.common;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.dialogapp.dialog.di.Injectable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Inject;

public class ListFragment extends BaseListFragment implements Injectable {
    public static final int TIMELINE = 0;
    public static final int MENTIONS = 1;
    public static final int FAVORITES = 2;
    private static String EXTRA_FRAGMENT = ListFragment.class.getName() + ".EXTRA_FRAGMENT";

    private int fragment;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

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
    protected void setViewModel() {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ListViewModel.class);
        switch (fragment) {
            case TIMELINE:
                viewModel.setView(BaseListViewModel.TIMELINE, null);
                break;
            case MENTIONS:
                viewModel.setView(BaseListViewModel.MENTIONS, null);
                break;
            case FAVORITES:
                viewModel.setView(BaseListViewModel.FAVORITES, null);
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TIMELINE, MENTIONS, FAVORITES})
    @interface FragmentTypeDef {
    }
}
