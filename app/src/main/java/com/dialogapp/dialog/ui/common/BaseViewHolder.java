package com.dialogapp.dialog.ui.common;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Generic ViewHolder base class
 *
 * @param <T> type of the item in the adapter
 */
public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {
    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void onBind(T item, int position);
}
