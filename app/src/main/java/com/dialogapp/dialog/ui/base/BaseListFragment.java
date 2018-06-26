package com.dialogapp.dialog.ui.base;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProvider;
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

import com.bumptech.glide.Glide;
import com.dialogapp.dialog.R;
import com.dialogapp.dialog.di.Injectable;
import com.dialogapp.dialog.model.Item;
import com.dialogapp.dialog.ui.common.ItemRecyclerAdapter;
import com.dialogapp.dialog.ui.common.ReplyActivity;
import com.dialogapp.dialog.ui.conversation.ConversationActivity;
import com.dialogapp.dialog.ui.imageviewer.ImageViewerActivity;
import com.dialogapp.dialog.ui.profilescreen.ProfileActivity;
import com.dialogapp.dialog.util.Status;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Base Fragment class for displaying lists
 */

public abstract class BaseListFragment extends Fragment implements Injectable, ItemRecyclerAdapter.PostOptionClickedListener {
    @BindView(R.id.recycler_list)
    protected RecyclerView recyclerView;

    @BindView(R.id.swipeContainer)
    protected SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.empty_list)
    protected TextView emptyPlaceholder;

    private FragmentEventListener listener;
    private Unbinder unbinder;
    private ItemRecyclerAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(true);
            onRefresh();
        });

        listener.getConnection().observe(requireActivity(), isConnected -> {
            if (swipeRefreshLayout != null) {
                if (isConnected != null && isConnected)
                    swipeRefreshLayout.setEnabled(true);
                else
                    swipeRefreshLayout.setEnabled(false);
            }
        });

        setupRecyclerView();
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
        listener.onFavoriteButtonClicked(postId, state);
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
    public void onImageClicked(String imageUrl) {
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

    public void scrollListToTop() {
        recyclerView.scrollToPosition(0);
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

    protected void onRefresh() {}

    protected void setupRecyclerView() {
        adapter = new ItemRecyclerAdapter(requireContext(), this,
                Glide.with(this), listener.shouldColorUsernameLinks());

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);
    }

    protected ViewModelProvider.Factory getViewModelFactory() {
        return listener.getViewModelFactory();
    }

    public interface FragmentEventListener {
        void onLoadSuccess(Object data);

        void onLoadError(String message);

        void onFavoriteButtonClicked(String postId, boolean state);

        ViewModelProvider.Factory getViewModelFactory();

        LiveData<Boolean> getConnection();

        boolean shouldColorUsernameLinks();
    }
}
