package com.dialogapp.dialog.ui.common;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.Spannable;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;
import android.view.View;

import com.dialogapp.dialog.ui.imageviewer.ImageViewerActivity;

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
            if (htmlString.charAt(start) == '@')
                htmlString.setSpan(new ForegroundColorSpan(Color.GREEN), start, end, 0);
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
