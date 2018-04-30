package com.dialogapp.dialog.ui.profilescreen;


import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.dialogapp.dialog.di.Injectable;
import com.dialogapp.dialog.ui.base.BaseListFragment;

import javax.inject.Inject;

import static com.dialogapp.dialog.ui.profilescreen.ProfileActivity.EXTRA_USERNAME;

public class ProfileFragment extends BaseListFragment implements Injectable {
    private String username;
    private ProfileViewModel viewModel;

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
    protected void setViewModel() {
        swipeRefreshLayout.setRefreshing(true);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ProfileViewModel.class);
        viewModel.setUsername(username);
        viewModel.getUserData().observe(this, microBlogResponseResource -> {
            if (microBlogResponseResource != null && microBlogResponseResource.data != null) {
                listener.onLoadSuccess(microBlogResponseResource.data);
            }
        });
        viewModel.getUserPosts().observe(this, listResource -> {
            setData(listResource.status, listResource.data, listResource.message);
        });
    }

    @Override
    protected void onViewRefreshed() {
        viewModel.refresh();
    }
}
