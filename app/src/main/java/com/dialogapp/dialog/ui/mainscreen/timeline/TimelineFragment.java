package com.dialogapp.dialog.ui.mainscreen.timeline;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.dialogapp.dialog.R;
import com.dialogapp.dialog.di.Injectable;
import com.dialogapp.dialog.ui.common.BaseListFragment;
import com.dialogapp.dialog.ui.common.PostViewHolder;
import com.dialogapp.dialog.util.InsetDividerDecoration;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class TimelineFragment extends BaseListFragment implements Injectable {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.swipe_refresh_list_layout, container, false);
        unbinder = ButterKnife.bind(this, view);

        swipeRefreshLayout.setOnRefreshListener(this::refresh);

        if (this.getContext() != null) {
            adapter = new TimelineAdapter(this.getContext(), Glide.with(this));
            recyclerView.setAdapter(adapter);
            recyclerView.setRecyclerListener(holder -> {
                ((PostViewHolder) holder).clearView();
            });
            recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
            recyclerView.addItemDecoration(new InsetDividerDecoration(this.getContext()));
        }

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        showLoadingProgress();
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(TimelineViewModel.class);
        viewModel.getPosts().observe(this, listResource -> {
            if (listResource != null) {
                setData(listResource.status, listResource.data, listResource.message);
            }
        });
    }
}
