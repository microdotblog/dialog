package com.dialogapp.dialog.ui.util

import android.content.Context
import com.dialogapp.dialog.util.NetworkUtil

class ImageGetterOptions(val context: Context,
                         var imageSize: ImageSize,
                         var preloadImages: Int = 0
) {

    fun shouldLoadOnWifi(): Boolean {
        return preloadImages == 1 && NetworkUtil.isConnectedToWifi(context)
    }
}