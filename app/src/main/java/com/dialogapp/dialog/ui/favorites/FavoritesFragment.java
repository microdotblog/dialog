package com.dialogapp.dialog.ui.favorites;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dialogapp.dialog.R;
import com.dialogapp.dialog.di.Injectable;
import com.dialogapp.dialog.ui.common.BaseListFragment;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class FavoritesFragment extends BaseListFragment implements Injectable {
    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.swipe_refresh_list_layout, container, false);
        unbinder = ButterKnife.bind(this, view);

        swipeRefreshLayout.setOnRefreshListener(this::load);

        if (this.getContext() != null) {
            adapter = new FavoritesAdapter(this.getContext());
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
            recyclerView.addItemDecoration(new DividerItemDecoration(this.getContext(), DividerItemDecoration.VERTICAL));
        }

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(FavoritesViewModel.class);
        viewModel.getPosts().observe(this, listResource -> {
            if (listResource != null) {
                setData(listResource.status, listResource.data, listResource.message);
            }
        });
        load();
    }
}