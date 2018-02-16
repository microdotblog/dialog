package com.dialogapp.dialog.ui.favorites;

import android.content.Context;
import android.view.ViewGroup;

import com.dialogapp.dialog.R;
import com.dialogapp.dialog.ui.common.BaseRecyclerAdapter;
import com.dialogapp.dialog.ui.common.BaseViewHolder;
import com.dialogapp.dialog.ui.common.PostViewHolder;

/**
 * Created by ekolocke on 16/2/18.
 */

public class FavoritesAdapter extends BaseRecyclerAdapter {

    public FavoritesAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PostViewHolder(inflate(R.layout.post_item, parent, false));
    }
}
