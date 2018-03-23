package com.dialogapp.dialog.ui.profilescreen;


import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dialogapp.dialog.R;
import com.dialogapp.dialog.di.Injectable;
import com.dialogapp.dialog.model.AccountResponse;
import com.dialogapp.dialog.ui.common.BaseListFragment;
import com.dialogapp.dialog.util.InsetDividerDecoration;
import com.dialogapp.dialog.util.Resource;
import com.dialogapp.dialog.util.Status;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.dialogapp.dialog.ui.profilescreen.ProfileActivity.EXTRA_USERNAME;

public class ProfileFragment extends BaseListFragment implements Injectable {
    private String username;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @BindView(R.id.image_profile)
    ImageView avatar;

    @BindView(R.id.text_profile_fullname)
    TextView fullname;

    @BindView(R.id.text_profile_email)
    TextView email;

    @BindView(R.id.text_profile_website)
    TextView website;

    @BindView(R.id.text_profile_about)
    TextView about;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        unbinder = ButterKnife.bind(this, view);

        if (this.getContext() != null) {
            adapter = new ProfileAdapter(this.getContext(), Glide.with(this));
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
            recyclerView.addItemDecoration(new InsetDividerDecoration(this.getContext()));
        }

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ProfileViewModel.class);
        viewModel.getPosts().observe(this, listResource -> {
            if (listResource != null) {
                setData(listResource.status, listResource.data, listResource.message);
            }
        });
        ((ProfileViewModel) viewModel).setUsername(username);

        AccountViewModel accountViewModel = ViewModelProviders.of(this, viewModelFactory).get(AccountViewModel.class);
        accountViewModel.getAccountData().observe(this, accountResponseResource -> {
            if (accountResponseResource != null) {
                if (accountResponseResource.data != null) {
                    setAccountData(accountResponseResource);
                }

                if (accountResponseResource.status == Status.ERROR)
                    listener.onLoadError(accountResponseResource.message);
            }
        });
        accountViewModel.setUsername(username);
    }

    private void setAccountData(Resource<AccountResponse> accountResponseResource) {
        Glide.with(this).load("https://micro.blog/" +
                accountResponseResource.data.getUsername() + "/avatar.jpg")
                .apply(new RequestOptions().circleCrop())
                .into(avatar);
        fullname.setText(accountResponseResource.data.getFullName());
        email.setText(accountResponseResource.data.getEmail());
        website.setText(accountResponseResource.data.getWebSite());
        about.setText(accountResponseResource.data.getAboutMe());
    }
}
