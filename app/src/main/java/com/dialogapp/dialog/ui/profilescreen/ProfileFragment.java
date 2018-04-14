package com.dialogapp.dialog.ui.profilescreen;


import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import com.dialogapp.dialog.di.Injectable;
import com.dialogapp.dialog.model.Item;
import com.dialogapp.dialog.ui.base.BaseListFragment;
import com.dialogapp.dialog.ui.common.ItemRecyclerAdapter;

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
    protected RecyclerView.Adapter<ItemRecyclerAdapter.PostViewHolder> getAdapter(Bundle savedInstanceState) {
        adapter = new ItemRecyclerAdapter(this.getActivity());
        return adapter;
    }

    @Override
    protected void setViewModel() {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ProfileViewModel.class);
        viewModel.setUsername(username);
        viewModel.getUserData().observe(this, microBlogResponseResource -> {
            if (microBlogResponseResource != null && microBlogResponseResource.data != null) {
                listener.onLoadSuccess(microBlogResponseResource.data);
                setData(microBlogResponseResource.status, microBlogResponseResource.data.items, microBlogResponseResource.message);
            }
        });
    }

    @Override
    protected void onViewRefreshed() {
        viewModel.refresh();
    }
}
