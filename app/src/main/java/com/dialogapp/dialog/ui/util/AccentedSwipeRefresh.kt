package com.dialogapp.dialog.ui.util

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dialogapp.dialog.R

class AccentedSwipeRefresh(context: Context, attrs: AttributeSet?) : SwipeRefreshLayout(context, attrs) {

    init {
        val typedValue = TypedValue()
        context.theme?.resolveAttribute(R.attr.colorAccent, typedValue, true)
        setColorSchemeColors(typedValue.data)
    }
}