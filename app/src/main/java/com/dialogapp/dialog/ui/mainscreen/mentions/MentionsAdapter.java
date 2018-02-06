package com.dialogapp.dialog.ui.mainscreen.mentions;

import android.content.Context;
import android.view.ViewGroup;

import com.dialogapp.dialog.R;
import com.dialogapp.dialog.model.Item;
import com.dialogapp.dialog.ui.mainscreen.common.BaseRecyclerAdapter;
import com.dialogapp.dialog.ui.mainscreen.common.PostViewHolder;

public class MentionsAdapter extends BaseRecyclerAdapter<Item, PostViewHolder> {

    public MentionsAdapter(Context context) {
        super(context);
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PostViewHolder(inflate(R.layout.post_item, parent, false));
    }
}
