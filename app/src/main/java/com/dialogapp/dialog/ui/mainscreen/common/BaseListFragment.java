package com.dialogapp.dialog.ui.mainscreen.common;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.dialogapp.dialog.R;
import com.dialogapp.dialog.model.Item;
import com.dialogapp.dialog.util.Status;

import java.util.List;

import butterknife.BindView;
import butterknife.Unbinder;

/**
 * Base Fragment class for displaying lists
 */

public abstract class BaseListFragment extends Fragment {
    protected Unbinder unbinder;
    protected BaseRecyclerAdapter adapter;
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    protected void setData(Status status, List<Item> data, String message) {
        if (status == Status.LOADING) {
            swipeRefreshLayout.setRefreshing(true);
            return;
        }

        if (data.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyPlaceholder.setVisibility(View.VISIBLE);
        } else {
            emptyPlaceholder.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            adapter.setItems(data);
        }
        swipeRefreshLayout.setRefreshing(false);

        if (status == Status.ERROR)
            listener.onLoadError(message);
    }

    public interface FragmentEventListener {
        void onLoadError(String message);
    }
}
