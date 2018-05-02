package com.dialogapp.dialog.ui.common;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.dialogapp.dialog.R;
import com.dialogapp.dialog.model.Item;
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
import de.hdodenhof.circleimageview.CircleImageView;
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

    Context context;
    PostItemOptionClickedListener listener;
    private RequestManager glide;
    private int currentNightMode;

    public ItemRecyclerAdapter(Context context) {
        super(DIFF_CALLBACK);
        this.context = context;
        this.glide = Glide.with(context);

        currentNightMode = context.getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_item, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Item item = getItem(position);

        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                holder.conversationButton.setImageAlpha(138);
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                holder.conversationButton.setImageAlpha(255);
                break;
            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                holder.conversationButton.setImageAlpha(138);
        }
        holder.conversationButton.setVisibility(getItem(position).microblog.isConversation ?
                View.VISIBLE : View.GONE);

        glide.load(item.author.avatar)
                .apply(RequestOptions.placeholderOf(R.color.grey400))
                .apply(RequestOptions.noAnimation())
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

    public void setListener(PostItemOptionClickedListener listener) {
        this.listener = listener;
    }

    public interface PostItemOptionClickedListener {
        void onAvatarClicked(String username);

        void onConversationButtonClicked(String postId);

        boolean onMenuItemClicked(int menuItemId, Item item);
    }

    public class PostViewHolder extends RecyclerView.ViewHolder implements PopupMenu.OnMenuItemClickListener {
        @BindView(R.id.image_thumbnail)
        CircleImageView thumbnail;

        @BindView(R.id.text_username)
        TextView username;

        @BindView(R.id.text_content)
        TextView content;

        @BindView(R.id.text_time)
        TextView time;

        @BindView(R.id.button_conversation)
        ImageButton conversationButton;

        @BindView(R.id.button_post_options)
        ImageButton optionsButtons;

        public PostViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            conversationButton.setOnClickListener(v -> {
                listener.onConversationButtonClicked(getItem(getAdapterPosition()).id);
            });

            thumbnail.setOnClickListener(view -> {
                listener.onAvatarClicked(getItem(getAdapterPosition()).author.microblog.username);
            });

            optionsButtons.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(context, v);
                popupMenu.setOnMenuItemClickListener(this);
                popupMenu.inflate(R.menu.post_options_popup);
                popupMenu.show();
            });
        }


        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            return listener.onMenuItemClicked(menuItem.getItemId(), getItem(getAdapterPosition()));
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
