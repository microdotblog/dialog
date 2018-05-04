package com.dialogapp.dialog.ui.mainscreen;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.dialogapp.dialog.model.Item;
import com.dialogapp.dialog.ui.base.BaseListFragment;
import com.dialogapp.dialog.util.Resource;

import java.util.List;

public class DiscoverFragment extends BaseListFragment {
    private DiscoverViewModel viewModel;
    private final Observer<Resource<List<Item>>> observer = listResource -> {
        if (listResource != null) {
            setData(listResource.status, listResource.data, listResource.message);
        }
    };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(DiscoverViewModel.class);
        viewModel.setTopic("");
        viewModel.getPosts().removeObserver(observer);
        viewModel.getPosts().observe(this, observer);
    }

    @Override
    protected void refresh() {
        super.refresh();
        viewModel.refresh();
    }
}
