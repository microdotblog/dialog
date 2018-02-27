package com.dialogapp.dialog.ui.mainscreen.timeline;

import android.content.Context;
import android.view.ViewGroup;

import com.bumptech.glide.RequestManager;
import com.dialogapp.dialog.R;
import com.dialogapp.dialog.model.Item;
import com.dialogapp.dialog.ui.common.BaseRecyclerAdapter;
import com.dialogapp.dialog.ui.common.PostViewHolder;

public class TimelineAdapter extends BaseRecyclerAdapter<Item, PostViewHolder> {

    private RequestManager glide;

    public TimelineAdapter(Context context, RequestManager glide) {
        super(context);
        this.glide = glide;
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PostViewHolder(inflate(R.layout.post_item, parent, false), glide);
    }
}
