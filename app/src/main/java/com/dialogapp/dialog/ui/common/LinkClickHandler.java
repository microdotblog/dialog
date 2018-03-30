package com.dialogapp.dialog.ui.common;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.util.TypedValue;
import android.view.View;

import com.dialogapp.dialog.R;
import com.dialogapp.dialog.ui.imageviewer.ImageViewerActivity;
import com.dialogapp.dialog.ui.profilescreen.ProfileActivity;

public class LinkClickHandler {

    public static void makeLinksClickable(Context context, Spannable htmlString) {
        ImageSpan[] images = htmlString.getSpans(0, htmlString.length(), ImageSpan.class);
        URLSpan[] urls = htmlString.getSpans(0, htmlString.length(), URLSpan.class);

        for (URLSpan span : urls) {
            int start = htmlString.getSpanStart(span);
            int end = htmlString.getSpanEnd(span);
            int flags = htmlString.getSpanFlags(span);
            ClickableSpan clickable = new ClickableSpan() {
                @Override
                public void onClick(View view) {
                    if (htmlString.charAt(start) == '@') {
                        Intent intent = new Intent(context, ProfileActivity.class);
                        intent.putExtra(ProfileActivity.EXTRA_USERNAME, htmlString.subSequence(start + 1, end).toString());
                        context.startActivity(intent);
                    } else {
                        Uri webpage = Uri.parse(span.getURL());
                        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                        if (intent.resolveActivity(context.getPackageManager()) != null) {
                            context.startActivity(intent);
                        }
                    }
                }
            };
            htmlString.setSpan(clickable, start, end, flags);
            if (htmlString.charAt(start) == '@') {
                // Remove underline
                htmlString.setSpan(new UnderlineSpan() {
                    public void updateDrawState(TextPaint tp) {
                        tp.setUnderlineText(false);
                    }
                }, start, end, 0);

                TypedValue value = new TypedValue();
                context.getTheme().resolveAttribute(R.attr.colorAccent, value, true);
                htmlString.setSpan(new ForegroundColorSpan(value.data), start, end, 0);
            }
            htmlString.removeSpan(span);
        }

        for (ImageSpan span : images) {
            int start = htmlString.getSpanStart(span);
            int end = htmlString.getSpanEnd(span);
            int flags = htmlString.getSpanFlags(span);
            ClickableSpan clickable = new ClickableSpan() {
                public void onClick(View view) {
                    Intent intent = new Intent(context, ImageViewerActivity.class);
                    intent.putExtra(ImageViewerActivity.EXTRA_IMAGE_URL, span.getSource());
                    context.startActivity(intent);
                }
            };
            htmlString.setSpan(clickable, start, end, flags);
        }
    }
}
