package com.dialogapp.dialog.ui.common;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.dialogapp.dialog.R;
import com.dialogapp.dialog.model.Item;
import com.dialogapp.dialog.ui.profilescreen.ProfileActivity;
import com.dialogapp.dialog.util.GlideImageGetter;
import com.dialogapp.dialog.util.Objects;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.text.Html.FROM_HTML_MODE_LEGACY;
import static android.util.TypedValue.COMPLEX_UNIT_DIP;
import static android.util.TypedValue.applyDimension;

public class ItemRecyclerAdapter extends ListAdapter<Item, ItemRecyclerAdapter.PostViewHolder> {
    public static final DiffUtil.ItemCallback<Item> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Item>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull Item oldItem, @NonNull Item newItem) {
                    return Objects.equals(oldItem.id, newItem.id);
                }

                @Override
                public boolean areContentsTheSame(
                        @NonNull Item oldItem, @NonNull Item newItem) {
                    return Objects.equals(oldItem.contentHtml, newItem.contentHtml);
                }
            };

    private Context context;
    private RequestManager glide;

    public ItemRecyclerAdapter(Context context) {
        super(DIFF_CALLBACK);
        this.context = context;
        this.glide = Glide.with(context);
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Item item = getItem(position);
        glide.load(item.author.avatar)
                .apply(RequestOptions.circleCropTransform())
                .into(holder.thumbnail);
        holder.username.setText(item.author.microblog.username);
        holder.time.setText(item.microblog.dateRelative);
        holder.bindHtmlContent(item.contentHtml);
    }

    @Override
    public void onViewRecycled(@NonNull PostViewHolder holder) {
        holder.clearView();
        super.onViewRecycled(holder);
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.image_thumbnail)
        ImageView thumbnail;

        @BindView(R.id.text_username)
        TextView username;

        @BindView(R.id.text_content)
        TextView content;

        @BindView(R.id.text_time)
        TextView time;

        public PostViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            thumbnail.setOnClickListener(view -> {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra(ProfileActivity.EXTRA_USERNAME, getItem(getAdapterPosition()).author.microblog.username);
                context.startActivity(intent);
            });
        }

        private void bindHtmlContent(String contentHtml) {
            CharSequence spannedText;
            Matcher matcher = Pattern.compile("<img").matcher(contentHtml);
            if (matcher.find()) {
                float size = applyDimension(COMPLEX_UNIT_DIP, 64, context.getResources().getDisplayMetrics());
                GlideImageGetter imageGetter = new GlideImageGetter(glide, content, (int) size);
                spannedText = getSpanned(contentHtml, imageGetter);
            } else {
                spannedText = getSpanned(contentHtml, null);
            }

            Spannable htmlString = (Spannable) spannedText;
            LinkClickHandler.makeLinksClickable(context, htmlString);
            content.setText(trimTrailingWhitespace(spannedText));
            content.setMovementMethod(LinkMovementMethod.getInstance());
        }

        private CharSequence getSpanned(String contentHtml, GlideImageGetter imageGetter) {
            if (Build.VERSION.SDK_INT >= 24) {
                return Html.fromHtml(contentHtml, FROM_HTML_MODE_LEGACY, imageGetter, null);
            } else {
                return Html.fromHtml(contentHtml, imageGetter, null);
            }
        }

        public void clearView() {
            glide.clear(thumbnail);
            username.setText(null);
            time.setText(null);
            GlideImageGetter.clear(content);
        }

        /**
         * Remove unwanted newlines added by Html.fromHtml
         *
         * @param source String with trailing whitespaces
         * @return String with trailing whitespaces removed
         */
        private CharSequence trimTrailingWhitespace(CharSequence source) {
            if (source == null)
                return "";

            int i = source.length();

            // loop back to the first non-whitespace character
            while (--i >= 0 && Character.isWhitespace(source.charAt(i))) {
            }

            return source.subSequence(0, i + 1);
        }
    }
}
