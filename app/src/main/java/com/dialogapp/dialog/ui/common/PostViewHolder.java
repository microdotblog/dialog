package com.dialogapp.dialog.ui.common;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.Html;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.dialogapp.dialog.R;
import com.dialogapp.dialog.model.Item;
import com.dialogapp.dialog.ui.profilescreen.ProfileActivity;
import com.dialogapp.dialog.util.GlideImageGetter;

import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.text.Html.FROM_HTML_MODE_LEGACY;
import static android.util.TypedValue.COMPLEX_UNIT_DIP;
import static android.util.TypedValue.applyDimension;

public class PostViewHolder extends BaseViewHolder<Item> {
    private Context context;
    private Pattern pattern;

    @BindView(R.id.image_thumbnail)
    ImageView thumbnail;

    @BindView(R.id.text_username)
    TextView username;

    @BindView(R.id.text_content)
    TextView content;

    @BindView(R.id.text_time)
    TextView time;

    public PostViewHolder(View itemView, RequestManager glide) {
        super(itemView, glide);
        ButterKnife.bind(this, itemView);

        this.context = this.itemView.getContext();
        this.pattern = Pattern.compile("<img");
    }

    @Override
    public void onBind(Item item, int position) {
        thumbnail.setOnClickListener(view -> {
            Intent intent = new Intent(context, ProfileActivity.class);
            intent.putExtra(ProfileActivity.EXTRA_USERNAME, item.author.microblog.username);
            context.startActivity(intent);
        });

        glide.load(item.author.avatar)
                .apply(RequestOptions.circleCropTransform())
                .into(thumbnail);

        username.setText(item.author.microblog.username);
        time.setText(item.microblog.dateRelative);

        bindHtmlContent(item.contentHtml);
    }

    private void bindHtmlContent(String contentHtml) {
        CharSequence spannedText;
        if (pattern.matcher(contentHtml).find()) {
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
     * @param source
     * @return
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
