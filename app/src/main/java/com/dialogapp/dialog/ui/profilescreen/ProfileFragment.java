package com.dialogapp.dialog.ui.profilescreen;


import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dialogapp.dialog.R;
import com.dialogapp.dialog.di.Injectable;
import com.dialogapp.dialog.model.Item;
import com.dialogapp.dialog.model.MicroBlogResponse;
import com.dialogapp.dialog.ui.common.BaseRecyclerAdapter;
import com.dialogapp.dialog.util.InsetDividerDecoration;
import com.dialogapp.dialog.util.Status;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.dialogapp.dialog.ui.profilescreen.ProfileActivity.EXTRA_USERNAME;

public class ProfileFragment extends Fragment implements Injectable {
    private Unbinder unbinder;
    private BaseRecyclerAdapter adapter;
    private ProfileViewModel viewModel;
    private String username;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @BindView(R.id.recycler_list)
    protected RecyclerView recyclerView;

    @BindView(R.id.empty_list)
    protected TextView emptyPlaceholder;

    @BindView(R.id.image_profile)
    ImageView avatar;

    @BindView(R.id.text_profile_fullname)
    TextView fullname;

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
        viewModel.getUserData().observe(this, microBlogResponseResource -> {
            if (microBlogResponseResource != null && microBlogResponseResource.data != null) {
                setAccountData(microBlogResponseResource.data);
                setData(microBlogResponseResource.status, microBlogResponseResource.data.items, microBlogResponseResource.message);
            }
        });
        viewModel.setUsername(username);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void setData(Status status, List<Item> data, String message) {
        // Set data ignoring the status
        if (data != null) {
            if (data.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                emptyPlaceholder.setVisibility(View.VISIBLE);
            } else {
                emptyPlaceholder.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

                adapter.setItems(data);
            }
        }
    }

    private void setAccountData(MicroBlogResponse accountResponseResource) {
        Glide.with(this).load(accountResponseResource.author.avatar)
                .apply(new RequestOptions().circleCrop())
                .into(avatar);
        fullname.setText(accountResponseResource.author.name);
        if (!accountResponseResource.author.url.isEmpty()) {
            website.setVisibility(View.VISIBLE);
            website.setText(accountResponseResource.author.url);
        }
        if (!accountResponseResource.microblog.bio.isEmpty()) {
            about.setVisibility(View.VISIBLE);
            about.setText(accountResponseResource.microblog.bio);

        }
    }
}
