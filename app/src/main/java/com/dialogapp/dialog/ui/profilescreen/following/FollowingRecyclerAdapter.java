package com.dialogapp.dialog.ui.profilescreen.following;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.dialogapp.dialog.R;
import com.dialogapp.dialog.model.FollowingAccountInfo;
import com.dialogapp.dialog.ui.profilescreen.ProfileActivity;
import com.dialogapp.dialog.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class FollowingRecyclerAdapter extends ListAdapter<FollowingAccountInfo, FollowingRecyclerAdapter.AccountInfoViewHolder> {
    private static final DiffUtil.ItemCallback<FollowingAccountInfo> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<FollowingAccountInfo>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull FollowingAccountInfo oldItem, @NonNull FollowingAccountInfo newItem) {
                    return Objects.equals(oldItem.name, newItem.name);
                }

                @Override
                public boolean areContentsTheSame(
                        @NonNull FollowingAccountInfo oldItem, @NonNull FollowingAccountInfo newItem) {
                    return Objects.equals(oldItem.username, newItem.username) &&
                            Objects.equals(oldItem.isFollowing, newItem.isFollowing) &&
                            Objects.equals(oldItem.avatar, oldItem.avatar);
                }
            };
    private RequestManager glide;

    protected FollowingRecyclerAdapter(RequestManager glide) {
        super(DIFF_CALLBACK);

        this.glide = glide;
    }

    @NonNull
    @Override
    public AccountInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.following_item, parent, false);
        AccountInfoViewHolder viewHolder = new AccountInfoViewHolder(view);

        viewHolder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ProfileActivity.class);
            intent.putExtra(ProfileActivity.EXTRA_USERNAME, getItem(viewHolder.getAdapterPosition()).username);
            view.getContext().startActivity(intent);
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AccountInfoViewHolder holder, int position) {
        glide.load(getItem(position).avatar)
                .apply(RequestOptions.placeholderOf(R.color.grey400))
                .apply(RequestOptions.noAnimation())
                .into(holder.thumbnail);
        holder.username.setText(getItem(position).username);
        holder.url.setText(getItem(position).url);
    }

    static class AccountInfoViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.image_following_avatar)
        CircleImageView thumbnail;

        @BindView(R.id.text_following_username)
        TextView username;

        @BindView(R.id.text_following_url)
        TextView url;

        public AccountInfoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
