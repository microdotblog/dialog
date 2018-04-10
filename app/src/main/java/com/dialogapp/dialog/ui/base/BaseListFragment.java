package com.dialogapp.dialog.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dialogapp.dialog.R;
import com.dialogapp.dialog.model.Item;
import com.dialogapp.dialog.util.Status;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Base Fragment class for displaying lists
 */

public abstract class BaseListFragment extends Fragment {
    protected Unbinder unbinder;
    protected FragmentEventListener listener;

    @BindView(R.id.recycler_list)
    protected RecyclerView recyclerView;

    @Nullable
    @BindView(R.id.swipeContainer)
    protected SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.empty_list)
    protected TextView emptyPlaceholder;

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

        swipeSetRefresh(true);
        setSwipeListener();
        setupRecyclerView();
        setViewModel();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
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

                setAdapterData(data);
            }
        }

        if (status == Status.ERROR)
            listener.onLoadError(message);

        if (status != Status.LOADING)
            swipeSetRefresh(false);
    }

    protected void refresh() {
        swipeSetRefresh(true);
        onViewRefreshed();
    }

    protected void setSwipeListener() {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setOnRefreshListener(this::refresh);
        }
    }

    protected abstract void setAdapterData(List<Item> data);

    protected abstract void setupRecyclerView();

    protected abstract void setViewModel();

    protected abstract void onViewRefreshed();

    private void swipeSetRefresh(boolean value) {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(value);
        }
    }

    public interface FragmentEventListener<T> {
        default void onLoadSuccess(T data) {
        }

        void onLoadError(String message);
    }
}
