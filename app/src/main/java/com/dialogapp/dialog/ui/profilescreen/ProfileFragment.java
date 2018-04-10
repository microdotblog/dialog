package com.dialogapp.dialog.ui.profilescreen;


import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.dialogapp.dialog.di.Injectable;
import com.dialogapp.dialog.model.Item;
import com.dialogapp.dialog.ui.base.BaseListFragment;
import com.dialogapp.dialog.ui.common.ItemRecyclerAdapter;
import com.dialogapp.dialog.ui.common.PostViewHolder;
import com.dialogapp.dialog.util.InsetDividerDecoration;

import java.util.List;

import javax.inject.Inject;

import static com.dialogapp.dialog.ui.profilescreen.ProfileActivity.EXTRA_USERNAME;

public class ProfileFragment extends BaseListFragment implements Injectable {
    private String username;
    private ProfileViewModel viewModel;
    private ItemRecyclerAdapter adapter;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    public static ProfileFragment newInstance(String username) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_USERNAME, username);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            username = getArguments().getString(EXTRA_USERNAME);
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
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ProfileViewModel.class);
        viewModel.setUsername(username, false);
        viewModel.getUserData().observe(this, microBlogResponseResource -> {
            if (microBlogResponseResource != null && microBlogResponseResource.data != null) {
                listener.onLoadSuccess(microBlogResponseResource.data);
                setData(microBlogResponseResource.status, microBlogResponseResource.data.items, microBlogResponseResource.message);
            }
        });
    }

    @Override
    protected void onViewRefreshed() {
        viewModel.setUsername(username, true);
    }
}
