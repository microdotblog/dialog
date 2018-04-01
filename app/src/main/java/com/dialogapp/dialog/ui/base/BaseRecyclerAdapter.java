package com.dialogapp.dialog.ui.base;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.RequestManager;
import com.dialogapp.dialog.R;
import com.dialogapp.dialog.model.Item;
import com.dialogapp.dialog.ui.common.PostViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView Adapter base class. Handles basic common logic.
 */
public class BaseRecyclerAdapter extends RecyclerView.Adapter<PostViewHolder> {

    private RequestManager glide;
    private List<Item> items;

    public BaseRecyclerAdapter(RequestManager glide) {
        items = new ArrayList<>();
        this.glide = glide;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_item, parent, false), glide);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        holder.onBind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public void setItems(List<Item> posts) {
        if (posts == null) {
            throw new IllegalArgumentException("Cannot set 'null' item in Recycler Adapter");
        }
        this.items.clear();
        this.items.addAll(posts);
        notifyDataSetChanged();
    }

    public List<Item> getItems() {
        return items;
    }

    public Item getItem(int position) {
        return items.get(position);
    }

    public void addItem(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Cannot add 'null' item to Recycler Adapter");
        }
        items.add(item);
        notifyItemInserted(items.size() - 1);
    }

    public void addAll(List<Item> items) {
        if (items == null) {
            throw new IllegalArgumentException("Cannot add 'null' items to Recycler Adapter");
        }
        this.items.addAll(items);
        notifyItemRangeInserted(this.items.size() - items.size(), items.size());
    }

    public void remove(Item item) {
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
}
