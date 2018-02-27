package com.dialogapp.dialog.ui.common;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bumptech.glide.RequestManager;

/**
 * Generic ViewHolder base class
 *
 * @param <T> type of the item in the adapter
 */
public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {
    protected RequestManager glide;

    public BaseViewHolder(View itemView, RequestManager glide) {
        super(itemView);
        this.glide = glide;
    }

    public abstract void onBind(T item, int position);

    public abstract void clearView();
}
