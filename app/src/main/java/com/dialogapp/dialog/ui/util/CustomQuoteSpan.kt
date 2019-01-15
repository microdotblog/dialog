package com.dialogapp.dialog.ui.util

import android.graphics.Canvas
import android.graphics.Paint
import android.text.Layout
import android.text.style.LeadingMarginSpan
import androidx.annotation.ColorInt

class CustomQuoteSpan(@param:ColorInt @get:ColorInt
                      val color: Int, stripeWidth: Int, gapWidth: Int) : LeadingMarginSpan {
    private var mStripWidth = 2
    private var mGapWidth = 2

    init {
        mStripWidth = stripeWidth
        mGapWidth = gapWidth
    }

    override fun getLeadingMargin(first: Boolean): Int {
        return mStripWidth + mGapWidth
    }

    override fun drawLeadingMargin(c: Canvas, p: Paint, x: Int, dir: Int,
                                   top: Int, baseline: Int, bottom: Int,
                                   text: CharSequence, start: Int, end: Int,
                                   first: Boolean, layout: Layout) {
        val style = p.style
        val color = p.color
        p.style = Paint.Style.FILL
        p.color = this.color
        c.drawRect(x.toFloat(), top.toFloat(), (x + dir * mStripWidth).toFloat(), bottom.toFloat(), p)
        p.style = style
        p.color = color
    }
}
