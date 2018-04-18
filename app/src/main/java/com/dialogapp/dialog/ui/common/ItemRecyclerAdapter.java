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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.dialogapp.dialog.R;
import com.dialogapp.dialog.model.Item;
import com.dialogapp.dialog.ui.conversation.ConversationActivity;
import com.dialogapp.dialog.ui.profilescreen.ProfileActivity;
import com.dialogapp.dialog.util.GlideImageGetter;
import com.dialogapp.dialog.util.Objects;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.LinkedList;
import java.util.Queue;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static android.text.Html.FROM_HTML_MODE_LEGACY;

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

    private int expandedPosition = -1;
    private int previousExpandedPosition = -1;

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
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_item, parent, false);
        final PostViewHolder viewHolder = new PostViewHolder(view);

        viewHolder.toggleButton.setOnClickListener(v -> {
            int position = viewHolder.getAdapterPosition();
            boolean isExpanded = position == expandedPosition;
            expandedPosition = isExpanded ? -1 : position;
            notifyItemChanged(previousExpandedPosition);
            notifyItemChanged(position);
        });

        viewHolder.conversationButton.setOnClickListener(v -> {
            long postId = getItem(viewHolder.getAdapterPosition()).id;
            Intent intent = new Intent(context, ConversationActivity.class);
            intent.putExtra(ConversationActivity.EXTRA_POST_ID, Long.toString(postId));
            context.startActivity(intent);
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Item item = getItem(position);

        if (item.microblog.isConversation) {
            holder.toggleButton.setVisibility(View.VISIBLE);
            final boolean isExpanded = position == expandedPosition;
            holder.postOptions.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            holder.itemView.setActivated(isExpanded);
            holder.toggleDrawable.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                    0, isExpanded ? R.drawable.ic_collapse_grey_24px : R.drawable.ic_expand_grey_24px);
            if (isExpanded)
                previousExpandedPosition = position;
        } else {
            holder.toggleButton.setVisibility(View.GONE);
            holder.postOptions.setVisibility(View.GONE);
        }

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

    public int getExpandedPosition() {
        return expandedPosition;
    }

    public void setExpandedPosition(int expandedPosition) {
        this.expandedPosition = expandedPosition;
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

        @BindView(R.id.button_toggle)
        FrameLayout toggleButton;

        @BindView(R.id.toggle)
        TextView toggleDrawable;

        @BindView(R.id.post_options)
        LinearLayout postOptions;

        @BindView(R.id.button_conversation)
        Button conversationButton;

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
            Document doc = Jsoup.parse(contentHtml);
            Elements images = doc.select("img");
            if (!images.isEmpty()) {
                Queue<Boolean> imagesQueue = getImages(images);
                GlideImageGetter imageGetter = new GlideImageGetter(glide, content, imagesQueue);
                spannedText = getSpanned(contentHtml, imageGetter);
            } else {
                spannedText = getSpanned(contentHtml, null);
            }

            Spannable htmlString = (Spannable) spannedText;
            LinkClickHandler.makeLinksClickable(context, htmlString);
            content.setText(trimTrailingWhitespace(spannedText));
            content.setMovementMethod(LinkMovementMethod.getInstance());
        }

        /**
         * Marks the images encountered while parsing the content as either large or small
         * <p>
         * An image is marked as small ('true') if the image tag has some particular class values or
         * the dimensions of the image, if present, are smaller than predefined dimensions.
         * 'false' otherwise.
         *
         * @param images list of image tags
         * @return list of booleans representing the size of the image
         */
        @NonNull
        private Queue<Boolean> getImages(Elements images) {
            Queue<Boolean> imageQueue = new LinkedList<>();
            for (Element img : images) {
                Timber.i(img.outerHtml());
                int width = -1, height = -1;
                boolean hasAttribs = false;
                if (img.hasAttr("width") && img.hasAttr("height")) {
                    hasAttribs = true;
                    width = Integer.parseInt(img.attributes().get("width"));
                    height = Integer.parseInt(img.attributes().get("height"));
                }

                if (img.hasClass("wp-smiley") || img.hasClass("mini_thumbnail") ||
                        (hasAttribs && (width > 0 && width <= 100) && (height > 0 && height <= 100)))
                    imageQueue.add(true);
                else
                    imageQueue.add(false);
            }
            return imageQueue;
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
