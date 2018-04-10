package com.dialogapp.dialog.ui.common;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.dialogapp.dialog.di.Injectable;
import com.dialogapp.dialog.model.Item;
import com.dialogapp.dialog.ui.base.BaseListFragment;
import com.dialogapp.dialog.ui.base.BaseListViewModel;
import com.dialogapp.dialog.util.InsetDividerDecoration;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import javax.inject.Inject;

public class ListFragment extends BaseListFragment implements Injectable {
    public static final int TIMELINE = 0;
    public static final int MENTIONS = 1;
    public static final int FAVORITES = 2;
    public static final int CONVERSATION = 3;
    public static final int DISCOVER = 4;

    private static final String EXTRA_ARG = ListFragment.class.getName() + ".EXTRA_ARG";
    private static final String EXTRA_FRAGMENT = ListFragment.class.getName() + ".EXTRA_FRAGMENT";

    private int fragment;
    private String postId;
    private ListViewModel viewModel;
    private ItemRecyclerAdapter adapter;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    public static Fragment newInstance(@FragmentTypeDef int fragment, String arg) {
        ListFragment listFragment = new ListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_FRAGMENT, fragment);
        bundle.putString(EXTRA_ARG, arg);
        listFragment.setArguments(bundle);
        return listFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            fragment = getArguments().getInt(EXTRA_FRAGMENT);
            postId = getArguments().getString(EXTRA_ARG);
        }
    }

    @Override
    protected void setAdapterData(List<Item> data) {
        adapter.submitList(data);
    }

    @Override
    protected void setupRecyclerView() {
        adapter = new ItemRecyclerAdapter(Glide.with(this));
        recyclerView.setAdapter(adapter);
        recyclerView.setRecyclerListener(holder -> ((PostViewHolder) holder).clearView());
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        recyclerView.addItemDecoration(new InsetDividerDecoration(this.getActivity()));
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
                break;
            case CONVERSATION:
                viewModel.setView(BaseListViewModel.CONVERSATION, postId);
                break;
            case DISCOVER:
                viewModel.setView(BaseListViewModel.DISCOVER, null);
        }
        viewModel.getPosts().observe(this, listResource -> {
            if (listResource != null) {
                setData(listResource.status, listResource.data, listResource.message);
            }
        });
    }

    @Override
    protected void onViewRefreshed() {
        viewModel.refresh();
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TIMELINE, MENTIONS, FAVORITES, CONVERSATION, DISCOVER})
    @interface FragmentTypeDef {
    }
}
