package com.dialogapp.dialog.ui.mainscreen.mentions;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dialogapp.dialog.R;
import com.dialogapp.dialog.model.Post;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MentionsAdapter extends RecyclerView.Adapter<MentionsAdapter.PostViewHolder> {

    private Context context;
    private List<Post> posts;

    public MentionsAdapter(Context context) {
        this.context = context;
        posts = new ArrayList<>();
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_item, parent, false);
        return new PostViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {
        Post item = posts.get(position);

        Glide.with(context)
                .load(item.author_avatar_url)
                .apply(RequestOptions.circleCropTransform())
                .into(holder.thumbnail);

        holder.username.setText(item.author_info_username);
        holder.content.setMovementMethod(LinkMovementMethod.getInstance());
        holder.content.setText(Html.fromHtml(item.contentHtml));
        holder.time.setText(item.post_property_dateRelative);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void setPosts(List<Post> posts) {
        this.posts.clear();
        this.posts.addAll(posts);
        notifyDataSetChanged();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.image_thumbnail)
        ImageView thumbnail;

        @BindView(R.id.text_username)
        TextView username;

        @BindView(R.id.text_content)
        TextView content;

        @BindView(R.id.image_content_pic)
        ImageView content_pic;

        @BindView(R.id.text_time)
        TextView time;

        public PostViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
