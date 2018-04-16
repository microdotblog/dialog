package com.dialogapp.dialog.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dialogapp.dialog.R;
import com.dialogapp.dialog.model.Item;
import com.dialogapp.dialog.util.InsetDividerDecoration;
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

        swipeRefreshLayout.setOnRefreshListener(this::refresh);

        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new InsetDividerDecoration(this.getActivity()));
        recyclerView.setAdapter(getAdapter(savedInstanceState));

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

        if (status == Status.LOADING)
            swipeRefreshLayout.setRefreshing(true);

        if (status == Status.ERROR)
            listener.onLoadError(message);

        if (status != Status.LOADING)
            swipeRefreshLayout.setRefreshing(false);
    }

    protected void refresh() {
        swipeRefreshLayout.setRefreshing(true);
        onViewRefreshed();
    }

    protected abstract void setAdapterData(List<Item> data);

    protected abstract RecyclerView.Adapter getAdapter(Bundle savedInstanceState);

    protected abstract void setViewModel();

    protected abstract void onViewRefreshed();

    public interface FragmentEventListener {
        default void onLoadSuccess(Object data) {
        }

        void onLoadError(String message);
    }
}
