package com.dialogapp.dialog.ui.profilescreen;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.dialogapp.dialog.ui.base.BaseListFragment;
import com.dialogapp.dialog.util.Status;

import static com.dialogapp.dialog.ui.profilescreen.ProfileActivity.EXTRA_USERNAME;

public class ProfileFragment extends BaseListFragment {
    private String username;
    private ProfileViewModel viewModel;
    private UserDataEventListener userDataEventListener;

    public static ProfileFragment newInstance(String username) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_USERNAME, username);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            userDataEventListener = (UserDataEventListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement UserDataEventListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            username = getArguments().getString(EXTRA_USERNAME);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = ViewModelProviders.of(this, getViewModelFactory()).get(ProfileViewModel.class);
        viewModel.setUsername(username);
        viewModel.getUserInfo().observe(this, userInfoResource -> {
            if (userInfoResource != null && userInfoResource.data != null) {
                userDataEventListener.onUserDataLoadSuccess(userInfoResource.data);
            }
        });
        viewModel.getUserPosts().observe(this, listResource -> {
            setData(listResource.status, listResource.data, listResource.message);
            if (listResource.status == Status.LOADING)
                userDataEventListener.onUserDataLoading();
        });
    }

    @Override
    protected void refresh() {
        super.refresh();
        viewModel.refresh();
    }

    public interface UserDataEventListener {
        void onUserDataLoadSuccess(UserInfo userInfo);

        void onUserDataLoading();
    }
}
