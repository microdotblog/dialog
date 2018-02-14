package com.dialogapp.dialog.ui.mainscreen.timeline;

import android.content.Context;
import android.view.ViewGroup;

import com.dialogapp.dialog.R;
import com.dialogapp.dialog.model.Item;
import com.dialogapp.dialog.ui.common.BaseRecyclerAdapter;
import com.dialogapp.dialog.ui.common.PostViewHolder;

public class TimelineAdapter extends BaseRecyclerAdapter<Item, PostViewHolder> {

    public TimelineAdapter(Context context) {
        super(context);
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PostViewHolder(inflate(R.layout.post_item, parent, false));
    }
}
