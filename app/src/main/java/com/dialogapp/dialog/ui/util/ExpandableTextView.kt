package com.dialogapp.dialog.ui.util

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView

import com.dialogapp.dialog.R

// https://stackoverflow.com/a/22751079/6024687
class ExpandableTextView : TextView, View.OnClickListener {

    private val defaultMaxLines = maxLines
    private var currentMaxLines: Int = Int.MAX_VALUE

    constructor(context: Context) : super(context) {
        setOnClickListener(this)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        setOnClickListener(this)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setOnClickListener(this)
    }

    override fun onTextChanged(text: CharSequence, start: Int, lengthBefore: Int, lengthAfter: Int) {
        post {
            if (lineCount > defaultMaxLines)
                setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.ic_round_more_horiz_24px)
            else
                setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)

            maxLines = maxLines
        }
    }

    override fun setMaxLines(maxLines: Int) {
        currentMaxLines = maxLines
        super.setMaxLines(maxLines)
    }

    override fun onClick(v: View) {
        /* Toggle between expanded collapsed states */
        maxLines = if (currentMaxLines == Integer.MAX_VALUE)
            defaultMaxLines
        else
            Integer.MAX_VALUE
    }
}