package com.dialogapp.dialog.ui.common;

import android.content.Context;
import android.text.Spannable;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;
import android.view.View;

public class LinkClickHandler {

    public static void makeLinksClickable(Context context, Spannable htmlString) {
        ImageSpan[] images = htmlString.getSpans(0, htmlString.length(), ImageSpan.class);
        URLSpan[] urls = htmlString.getSpans(0, htmlString.length(), URLSpan.class);

        for (URLSpan span : urls) {
            int start = htmlString.getSpanStart(span);
            int end = htmlString.getSpanEnd(span);
            int flags = htmlString.getSpanFlags(span);
            ClickableSpan clickable = new ClickableSpan() {
                public void onClick(View view) {

                }
            };
            htmlString.setSpan(clickable, start, end, flags);
            htmlString.removeSpan(span);
        }

        for (ImageSpan span : images) {
            int start = htmlString.getSpanStart(span);
            int end = htmlString.getSpanEnd(span);
            int flags = htmlString.getSpanFlags(span);
            ClickableSpan clickable = new ClickableSpan() {
                public void onClick(View view) {

                }
            };
            htmlString.setSpan(clickable, start, end, flags);
        }
    }
}
