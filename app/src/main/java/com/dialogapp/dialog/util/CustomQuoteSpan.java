package com.dialogapp.dialog.util;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.text.Layout;
import android.text.style.LeadingMarginSpan;

public class CustomQuoteSpan implements LeadingMarginSpan {
    private int mStripWidth = 2;
    private int mGapWidth = 2;
    private final int mColor;

    public CustomQuoteSpan(@ColorInt int color, int stripeWidth, int gapWidth) {
        super();
        mColor = color;
        mStripWidth = stripeWidth;
        mGapWidth = gapWidth;
    }

    @Override
    public int getLeadingMargin(boolean first) {
        return mStripWidth + mGapWidth;
    }

    @Override
    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir,
                                  int top, int baseline, int bottom,
                                  CharSequence text, int start, int end,
                                  boolean first, Layout layout) {
        Paint.Style style = p.getStyle();
        int color = p.getColor();
        p.setStyle(Paint.Style.FILL);
        p.setColor(mColor);
        c.drawRect(x, top, x + dir * mStripWidth, bottom, p);
        p.setStyle(style);
        p.setColor(color);
    }

    @ColorInt
    public int getColor() {
        return mColor;
    }
}
