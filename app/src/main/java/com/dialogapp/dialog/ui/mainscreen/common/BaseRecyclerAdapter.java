package com.dialogapp.dialog.ui.mainscreen.common;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Generic RecyclerView Adapter base class. Handles basic common logic.
 *
 * @param <T>  type of the items to be used in the adapter's dataset
 * @param <VH> ViewHolder {@link BaseViewHolder}
 */
public abstract class BaseRecyclerAdapter<T, VH extends BaseViewHolder<T>>
        extends RecyclerView.Adapter<VH> {

    private LayoutInflater layoutInflater;
    private List<T> items;

    public BaseRecyclerAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
        items = new ArrayList<>();
    }

    /**
     * To be implemented in the subclass
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public abstract VH onCreateViewHolder(ViewGroup parent, int viewType);

    @SuppressWarnings("unchecked")
    @Override
    public void onBindViewHolder(VH holder, int position) {
        holder.onBind(items.get(position), position);
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public void setItems(List<T> posts) {
        if (posts == null) {
            throw new IllegalArgumentException("Cannot set 'null' item in Recycler Adapter");
        }
        this.items.clear();
        this.items.addAll(posts);
        notifyDataSetChanged();
    }

    public List<T> getItems() {
        return items;
    }

    public T getItem(int position) {
        return items.get(position);
    }

    public void addItem(T item) {
        if (item == null) {
            throw new IllegalArgumentException("Cannot add 'null' item to Recycler Adapter");
        }
        items.add(item);
        notifyItemInserted(items.size() - 1);
    }

    public void addAll(List<T> items) {
        if (items == null) {
            throw new IllegalArgumentException("Cannot add 'null' items to Recycler Adapter");
        }
        this.items.addAll(items);
        notifyItemRangeInserted(this.items.size() - items.size(), items.size());
    }

    public void remove(T item) {
        int position = items.indexOf(item);
        if (position > -1) {
            items.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    @NonNull
    protected View inflate(@LayoutRes final int layout, @Nullable final ViewGroup parent) {
        return inflate(layout, parent, false);
    }

    @NonNull
    protected View inflate(@LayoutRes final int layout, @Nullable final ViewGroup parent,
                           final boolean attachToRoot) {
        return layoutInflater.inflate(layout, parent, attachToRoot);
    }
}
