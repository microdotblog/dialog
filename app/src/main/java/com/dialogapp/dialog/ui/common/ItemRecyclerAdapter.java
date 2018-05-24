package com.dialogapp.dialog.ui.common;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.QuoteSpan;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.dialogapp.dialog.R;
import com.dialogapp.dialog.model.Item;
import com.dialogapp.dialog.util.CustomQuoteSpan;
import com.dialogapp.dialog.util.GlideImageGetter;
import com.dialogapp.dialog.util.Objects;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import timber.log.Timber;

import static android.text.Html.FROM_HTML_MODE_LEGACY;

public class ItemRecyclerAdapter extends ListAdapter<Item, ItemRecyclerAdapter.PostViewHolder> {
    private static final DiffUtil.ItemCallback<Item> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Item>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull Item oldItem, @NonNull Item newItem) {
                    return Objects.equals(oldItem.id, newItem.id) &&
                            Objects.equals(oldItem.author.microblog.username, newItem.author.microblog.username);
                }

                @Override
                public boolean areContentsTheSame(
                        @NonNull Item oldItem, @NonNull Item newItem) {
                    return Objects.equals(oldItem.microblog.isFavorite, newItem.microblog.isFavorite) &&
                            Objects.equals(oldItem.microblog.isConversation, newItem.microblog.isConversation) &&
                            Objects.equals(oldItem.microblog.dateRelative, newItem.microblog.dateRelative) &&
                            Objects.equals(oldItem.author.avatar, newItem.author.avatar) &&
                            Objects.equals(oldItem.author.microblog.username, newItem.author.microblog.username) &&
                            Objects.equals(oldItem.contentHtml, newItem.contentHtml);
                }

                @Override
                public Object getChangePayload(Item oldItem, Item newItem) {
                    Bundle diffBundle = new Bundle();

                    if (!Objects.equals(oldItem.microblog.isFavorite, newItem.microblog.isFavorite))
                        diffBundle.putBoolean("FAV", newItem.microblog.isFavorite);

                    if (!Objects.equals(oldItem.microblog.isConversation, newItem.microblog.isConversation))
                        diffBundle.putBoolean("CONV", newItem.microblog.isConversation);

                    if (!Objects.equals(oldItem.microblog.dateRelative, newItem.microblog.dateRelative))
                        diffBundle.putString("DATE", newItem.microblog.dateRelative);

                    if (!Objects.equals(oldItem.author.avatar, newItem.author.avatar))
                        diffBundle.putString("AVATAR", newItem.author.avatar);

                    if (!Objects.equals(oldItem.author.microblog.username, newItem.author.microblog.username))
                        diffBundle.putString("USERNAME", newItem.author.microblog.username);

                    if (!Objects.equals(oldItem.contentHtml, newItem.contentHtml))
                        diffBundle.putString("CONTENT", newItem.contentHtml);

                    return (diffBundle.size() == 0) ? null : diffBundle;
                }
            };

    Context context;
    PostItemOptionClickedListener listener;
    private RequestManager glide;
    private int currentNightMode;

    public ItemRecyclerAdapter(Context context, RequestManager glide) {
        super(DIFF_CALLBACK);
        this.context = context;
        this.glide = glide;

        currentNightMode = context.getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_item, parent, false);
        return new PostViewHolder(view, glide, this);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            Bundle diffBundle = (Bundle) payloads.get(0);

            for (String key : diffBundle.keySet()) {
                switch (key) {
                    case "CONV":
                        holder.conversationButton.setVisibility(diffBundle.getBoolean("CONV") ?
                                View.VISIBLE : View.GONE);
                        break;
                    case "FAV":
                        if (diffBundle.getBoolean("FAV")) {
                            holder.favoriteButton.setImageResource(R.drawable.ic_star_black_24dp);
                            holder.favoriteButton.setImageAlpha(255);
                            holder.favoriteButton.setColorFilter(ContextCompat.getColor(context, R.color.reda200));
                        } else {
                            holder.favoriteButton.setImageResource(R.drawable.ic_star_border_black_24dp);
                            holder.favoriteButton.setColorFilter(isNight() ? Color.WHITE : Color.BLACK);
                            setIconAlpha(holder.favoriteButton);
                        }
                        break;
                    case "DATE":
                        holder.time.setText(diffBundle.getString("DATE"));
                        break;
                    case "AVATAR":
                        glide.load(diffBundle.getString("AVATAR"))
                                .apply(RequestOptions.placeholderOf(R.color.grey400))
                                .apply(RequestOptions.noAnimation())
                                .into(holder.thumbnail);
                        break;
                    case "USERNAME":
                        holder.username.setText(diffBundle.getString("USERNAME"));
                        break;
                    case "CONTENT":
                        holder.bindHtmlContent(diffBundle.getString("CONTENT"));
                        break;
                }
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Item item = getItem(position);

        setIconAlpha(holder.conversationButton);
        holder.conversationButton.setColorFilter(isNight() ? Color.WHITE : Color.BLACK);
        holder.conversationButton.setVisibility(getItem(position).microblog.isConversation ?
                View.VISIBLE : View.GONE);

        holder.replyButton.setColorFilter(isNight() ? Color.WHITE : Color.BLACK);
        setIconAlpha(holder.replyButton);

        if (item.microblog.isFavorite) {
            holder.favoriteButton.setImageResource(R.drawable.ic_star_black_24dp);
            holder.favoriteButton.setImageAlpha(255);
            holder.favoriteButton.setColorFilter(ContextCompat.getColor(context, R.color.reda200));
        } else {
            holder.favoriteButton.setImageResource(R.drawable.ic_star_border_black_24dp);
            holder.favoriteButton.setColorFilter(isNight() ? Color.WHITE : Color.BLACK);
            setIconAlpha(holder.favoriteButton);
        }

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

    private void setIconAlpha(ImageButton button) {
        if (isNight())
            button.setImageAlpha(255);
        else
            button.setImageAlpha(138);
    }

    private boolean isNight() {
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES;
    }

    public interface PostItemOptionClickedListener {
        void onAvatarClicked(String username);

        void onFavoriteButtonClicked(String postId, boolean state);

        void onConversationButtonClicked(String postId);

        boolean onMenuItemClicked(int menuItemId, Item item);

        void onReplyButtonClicked(String id, String username);

        void onLinkClicked(boolean isInternalLink, String text);

        void onImageClicked(String imageUrl);
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.image_thumbnail)
        CircleImageView thumbnail;

        @BindView(R.id.text_username)
        TextView username;

        @BindView(R.id.text_content)
        TextView content;

        @BindView(R.id.text_time)
        TextView time;

        @BindView(R.id.button_reply_send)
        ImageButton replyButton;

        @BindView(R.id.button_conversation)
        ImageButton conversationButton;

        @BindView(R.id.button_favorite)
        ImageButton favoriteButton;

        @BindView(R.id.button_post_options)
        ImageButton optionsButtons;

        private RequestManager glide;
        private ItemRecyclerAdapter adapter;

        public PostViewHolder(View itemView, RequestManager glide, ItemRecyclerAdapter adapter) {
            super(itemView);
            this.glide = glide;
            this.adapter = adapter;
            ButterKnife.bind(this, itemView);

            favoriteButton.setOnClickListener(v -> {
                adapter.listener.onFavoriteButtonClicked(adapter.getItem(getAdapterPosition()).id,
                        !adapter.getItem(getAdapterPosition()).microblog.isFavorite);
            });

            conversationButton.setOnClickListener(v -> {
                adapter.listener.onConversationButtonClicked(adapter.getItem(getAdapterPosition()).id);
            });

            replyButton.setOnClickListener(v -> {
                adapter.listener.onReplyButtonClicked(adapter.getItem(getAdapterPosition()).id,
                        adapter.getItem(getAdapterPosition()).author.microblog.username);
            });

            thumbnail.setOnClickListener(view -> {
                adapter.listener.onAvatarClicked(adapter.getItem(getAdapterPosition()).author.microblog.username);
            });

            optionsButtons.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                popupMenu.setOnMenuItemClickListener(item -> {
                    adapter.listener.onMenuItemClicked(item.getItemId(), adapter.getItem(getAdapterPosition()));
                    return true;
                });
                popupMenu.inflate(R.menu.post_options_popup);
                popupMenu.show();
            });
        }

        void bindHtmlContent(String contentHtml) {
            SpannableString spannedText;
            Elements images = Jsoup.parse(contentHtml).select("img");
            GlideImageGetter imageGetter = null;
            if (!images.isEmpty()) {
                Queue<Boolean> imagesQueue = getImages(images);
                imageGetter = new GlideImageGetter(glide, content, imagesQueue);
            }
            spannedText = getSpanned(contentHtml, imageGetter);

            setSpans(spannedText);
            content.setText(trimTrailingWhitespace(spannedText));
            content.setMovementMethod(LinkMovementMethod.getInstance());
        }

        private void setSpans(SpannableString text) {
            for (final ImageSpan span : text.getSpans(0, text.length(), ImageSpan.class)) {
                int flags = text.getSpanFlags(span);
                int start = text.getSpanStart(span);
                int end = text.getSpanEnd(span);

                text.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        adapter.listener.onImageClicked(span.getSource());
                    }
                }, start, end, flags);
            }

            for (URLSpan span : text.getSpans(0, text.length(), URLSpan.class)) {
                int start = text.getSpanStart(span);
                int end = text.getSpanEnd(span);
                int flags = text.getSpanFlags(span);

                text.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View view) {
                        if (text.charAt(start) == '@')
                            adapter.listener.onLinkClicked(true, text.subSequence(start + 1, end).toString());
                        else
                            adapter.listener.onLinkClicked(false, span.getURL());
                    }
                }, start, end, flags);

                if (text.charAt(start) == '@') {
                    text.removeSpan(span);
                    text.setSpan(new URLSpan(span.getURL()) {
                        @Override
                        public void updateDrawState(TextPaint ds) {
                            super.updateDrawState(ds);
                            ds.setUnderlineText(false);
                        }
                    }, start, end, 0);

                    if (adapter.isNight())
                        text.setSpan(new ForegroundColorSpan(Color.WHITE), start, end, flags);
                    else
                        text.setSpan(new ForegroundColorSpan(ContextCompat.getColor(adapter.context, R.color.primary_text_default_material_light)), start, end, flags);
                }
            }

            int primaryColor = ResourcesCompat.getColor(adapter.context.getResources(), R.color.reda200, null);

            for(QuoteSpan span : text.getSpans(0, text.length(), QuoteSpan.class)) {
                CustomQuoteSpan quoteSpan = new CustomQuoteSpan(primaryColor, 4, 8);
                int start = text.getSpanStart(span);
                int end = text.getSpanEnd(span);
                text.removeSpan(span);
                text.setSpan(quoteSpan, start, end, 0);
            }
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
                    try {
                        width = Integer.parseInt(img.attributes().get("width"));
                        height = Integer.parseInt(img.attributes().get("height"));
                        hasAttribs = true;
                    } catch (NumberFormatException e) {
                        hasAttribs = false;
                    }
                }

                if (img.hasClass("wp-smiley") || img.hasClass("mini_thumbnail") ||
                        (hasAttribs && (width > 0 && width <= 100) && (height > 0 && height <= 100)))
                    imageQueue.add(true);
                else
                    imageQueue.add(false);
            }
            return imageQueue;
        }

        private SpannableString getSpanned(String contentHtml, GlideImageGetter imageGetter) {
            if (Build.VERSION.SDK_INT >= 24) {
                return new SpannableString(Html.fromHtml(contentHtml, FROM_HTML_MODE_LEGACY, imageGetter, null));
            } else {
                return new SpannableString(Html.fromHtml(contentHtml, imageGetter, null));
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
