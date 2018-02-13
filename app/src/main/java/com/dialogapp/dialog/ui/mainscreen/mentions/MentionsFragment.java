package com.dialogapp.dialog.ui.mainscreen.mentions;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dialogapp.dialog.R;
import com.dialogapp.dialog.di.Injectable;
import com.dialogapp.dialog.ui.mainscreen.common.BaseListFragment;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class MentionsFragment extends BaseListFragment implements Injectable {

    private MentionsViewModel mentionsViewModel;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.common_list_layout, container, false);
        unbinder = ButterKnife.bind(this, view);

        swipeRefreshLayout.setOnRefreshListener(this::load);

        adapter = new MentionsAdapter(this.getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mentionsViewModel = ViewModelProviders.of(this, viewModelFactory).get(MentionsViewModel.class);
        mentionsViewModel.getMentionsPosts().observe(this, listResource -> {
            if (listResource != null) {
                setData(listResource.status, listResource.data, listResource.message);
            }
        });
        load();
    }

    private void load() {
        swipeRefreshLayout.setRefreshing(true);
        adapter.clear();
        mentionsViewModel.refresh();
    }
}
