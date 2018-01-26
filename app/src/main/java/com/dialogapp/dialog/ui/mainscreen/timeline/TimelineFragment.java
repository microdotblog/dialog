package com.dialogapp.dialog.ui.mainscreen.timeline;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dialogapp.dialog.R;
import com.dialogapp.dialog.di.Injectable;
import com.dialogapp.dialog.util.Status;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class TimelineFragment extends Fragment implements Injectable {

    private Unbinder unbinder;
    private TimelineViewModel timelineViewModel;
    private TimelineAdapter adapter;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @BindView(R.id.recycler_list)
    RecyclerView recyclerView;

    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.common_list_layout, container, false);
        unbinder = ButterKnife.bind(this, view);

        adapter = new TimelineAdapter(this.getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        timelineViewModel = ViewModelProviders.of(this, viewModelFactory).get(TimelineViewModel.class);
        timelineViewModel.refresh();
        timelineViewModel.getTimelinePosts().observe(this, listResource -> {
            if (listResource != null) {
                if (listResource.status == Status.SUCCESS)
                    adapter.setPosts(listResource.data);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
