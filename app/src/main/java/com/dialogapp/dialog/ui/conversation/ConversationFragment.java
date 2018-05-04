package com.dialogapp.dialog.ui.conversation;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.dialogapp.dialog.ui.base.BaseListFragment;

public class ConversationFragment extends BaseListFragment {
    private static final String EXTRA_ARG = ConversationFragment.class.getName() + ".EXTRA_ARG";
    private String postId;
    private ConversationViewModel viewModel;

    public static Fragment newInstance(String postId) {
        ConversationFragment fragment = new ConversationFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_ARG, postId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            postId = getArguments().getString(EXTRA_ARG);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ConversationViewModel.class);
        viewModel.setPostId(postId);
        viewModel.getPosts().observe(this, listResource -> {
            if (listResource != null) {
                setData(listResource.status, listResource.data, listResource.message);
            }
        });
    }

    @Override
    protected void refresh() {
        super.refresh();
        viewModel.refresh();
    }
}
