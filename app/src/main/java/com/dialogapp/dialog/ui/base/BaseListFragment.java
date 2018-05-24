package com.dialogapp.dialog.ui.base;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dialogapp.dialog.R;
import com.dialogapp.dialog.di.Injectable;
import com.dialogapp.dialog.model.Item;
import com.dialogapp.dialog.ui.common.ItemRecyclerAdapter;
import com.dialogapp.dialog.ui.common.ReplyActivity;
import com.dialogapp.dialog.ui.common.RequestViewModel;
import com.dialogapp.dialog.ui.conversation.ConversationActivity;
import com.dialogapp.dialog.ui.imageviewer.ImageViewerActivity;
import com.dialogapp.dialog.ui.profilescreen.ProfileActivity;
import com.dialogapp.dialog.util.Event;
import com.dialogapp.dialog.util.Resource;
import com.dialogapp.dialog.util.Status;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Base Fragment class for displaying lists
 */

public abstract class BaseListFragment extends Fragment implements Injectable, ItemRecyclerAdapter.PostItemOptionClickedListener {
    protected Unbinder unbinder;
    protected FragmentEventListener listener;
    protected ItemRecyclerAdapter adapter;

    @BindView(R.id.recycler_list)
    protected RecyclerView recyclerView;

    @BindView(R.id.swipeContainer)
    protected SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.empty_list)
    protected TextView emptyPlaceholder;

    @Inject
    protected ViewModelProvider.Factory viewModelFactory;

    protected RequestViewModel requestViewModel;

    private final Observer<Event<Resource<Boolean>>> requestObserver = booleanResource -> {
        if (booleanResource.getContentIfNotHandled() != null) {
            if (booleanResource.peekContent().data) {
                Toast.makeText(getActivity(), R.string.request_successful, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), R.string.request_unsuccessful, Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            listener = (FragmentEventListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement FragmentEventListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.swipe_refresh_list_layout, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        swipeRefreshLayout.setOnRefreshListener(this::refresh);

        ((BaseNetworkWatcherActivity) getActivity()).connectionViewModel
                .getConnectionStatus().observe(getActivity(), isConnected -> {
            if (isConnected != null && isConnected)
                swipeRefreshLayout.setEnabled(true);
            else
                swipeRefreshLayout.setEnabled(false);
        });

        adapter = new ItemRecyclerAdapter(this.getActivity(), Glide.with(this));
        adapter.setListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(this.getActivity(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);

        requestViewModel = ViewModelProviders.of(this, viewModelFactory).get(RequestViewModel.class);
        requestViewModel.getResponseFavorite().removeObserver(requestObserver);
        requestViewModel.getResponseFavorite().observe(this, requestObserver);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onAvatarClicked(String username) {
        Intent intent = new Intent(getActivity(), ProfileActivity.class);
        intent.putExtra(ProfileActivity.EXTRA_USERNAME, username);
        startActivity(intent);
    }

    @Override
    public void onFavoriteButtonClicked(String postId, boolean state) {
        Toast.makeText(getActivity(), "Requesting...", Toast.LENGTH_SHORT).show();
        requestViewModel.setFavoriteState(postId, state);
    }

    @Override
    public void onConversationButtonClicked(String postId) {
        Intent intent = new Intent(getActivity(), ConversationActivity.class);
        intent.putExtra(ConversationActivity.EXTRA_POST_ID, postId);
        startActivity(intent);
    }

    @Override
    public void onReplyButtonClicked(String postId, String username) {
        Intent intent = new Intent(getActivity(), ReplyActivity.class);
        intent.putExtra(ReplyActivity.EXTRA_POSTID, postId);
        intent.putExtra(ReplyActivity.EXTRA_USERNAME, username);
        startActivity(intent);
    }

    @Override
    public void onLinkClicked(boolean isInternalLink, String text) {
        if (isInternalLink) {
            Intent intent = new Intent(getActivity(), ProfileActivity.class);
            intent.putExtra(ProfileActivity.EXTRA_USERNAME, text);
            startActivity(intent);
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(text));
            startActivity(intent);
        }
    }

    @Override
    public void onImageClick(String imageUrl) {
        Intent intent = new Intent(getActivity(), ImageViewerActivity.class);
        intent.putExtra(ImageViewerActivity.EXTRA_IMAGE_URL, imageUrl);
        startActivity(intent);
    }

    @Override
    public boolean onMenuItemClicked(int menuItemId, Item item) {
        switch (menuItemId) {
            case R.id.post_option_view_link:
                Uri webpage = Uri.parse(item.url);
                Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                }
                return true;
            default:
                return false;
        }
    }

    protected void setData(Status status, List<Item> data, String message) {
        // Set data ignoring the status
        if (data != null) {
            if (data.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                emptyPlaceholder.setVisibility(View.VISIBLE);
            } else {
                emptyPlaceholder.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

                adapter.submitList(data);
            }
        }

        if (status == Status.LOADING)
            swipeRefreshLayout.setRefreshing(true);

        if (status == Status.ERROR)
            listener.onLoadError(message);

        if (status != Status.LOADING)
            swipeRefreshLayout.setRefreshing(false);
    }

    protected void refresh() {
        swipeRefreshLayout.setRefreshing(true);
    }

    public interface FragmentEventListener {
        void onLoadSuccess(Object data);

        void onLoadError(String message);
    }
}
