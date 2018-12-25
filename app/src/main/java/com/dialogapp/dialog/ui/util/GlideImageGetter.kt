package com.dialogapp.dialog.ui.util

import android.content.res.Resources
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Animatable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.text.Html
import android.util.TypedValue
import android.view.Gravity
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.dialogapp.dialog.GlideRequests
import com.dialogapp.dialog.R
import com.dialogapp.dialog.util.DrawableWrapper
import java.util.*

/**
 * Custom ImageGetter using Glide 4
 *
 *
 * Adapted from https://github.com/TWiStErRob/glide-support/commit/c3de297d0daec8d2650a3a640c0404c35b937a30
 */
class GlideImageGetter(private val glide: GlideRequests,
                       private val targetView: TextView,
                       private val imageSize: ImageSize,
                       private val imageTypes: Queue<ImageType>) : Html.ImageGetter, Drawable.Callback {
    private var request: RequestBuilder<Drawable>
    private val context = targetView.context.applicationContext
    private val imageTargets = ArrayList<Target<*>>()

    companion object {
        private const val DEFAULT_WIDTH_PX = 48
        private const val DEFAULT_HEIGHT_PX = 48

        private const val IMAGE_SMALL_HEIGHT_DP = 96
        private const val IMAGE_SMALL_WIDTH_DP = 96

        private const val IMAGE_MEDIUM_HEIGHT_DP = 178
        private const val IMAGE_MEDIUM_WIDTH_DP = 178

        private const val IMAGE_LARGE_HEIGHT_DP = 256
        private const val IMAGE_LARGE_WIDTH_DP = 256

        fun clear(view: TextView) {
            view.text = null
            val tag = view.tag
            if (tag is GlideImageGetter) {
                tag.clear()
                view.tag = null
            }
        }
    }

    init {
        request = createGlideRequest(glide)
        targetView.tag = this
    }

    override fun getDrawable(url: String?): Drawable {
        val drawableWidth: Int
        val drawableHeight: Int

        val type = imageTypes.remove()
        when (type) {
            ImageType.EMOJI -> {
                drawableWidth = DEFAULT_WIDTH_PX
                drawableHeight = DEFAULT_HEIGHT_PX
            }
            else -> when (imageSize) {
                ImageSize.SMALL -> {
                    drawableWidth = dpToPx(IMAGE_SMALL_WIDTH_DP)
                    drawableHeight = dpToPx(IMAGE_SMALL_HEIGHT_DP)
                }
                ImageSize.MEDIUM -> {
                    drawableWidth = dpToPx(IMAGE_MEDIUM_WIDTH_DP)
                    drawableHeight = dpToPx(IMAGE_MEDIUM_HEIGHT_DP)
                }
                ImageSize.LARGE -> {
                    drawableWidth = dpToPx(IMAGE_LARGE_WIDTH_DP)
                    drawableHeight = dpToPx(IMAGE_LARGE_HEIGHT_DP)
                }
            }
        }
        // set up target for this Image inside the TextView
        val imageTarget = WrapperTarget(drawableWidth, drawableHeight)
        val asyncWrapper = imageTarget.lazyDrawable
        // listen for Drawable's request for invalidation
        asyncWrapper.callback = this

        // start Glide's async load
        request.load(url).into(imageTarget)
        // save target for clearing it later
        imageTargets.add(imageTarget)
        return asyncWrapper
    }

    override fun invalidateDrawable(@NonNull drawable: Drawable) {
        targetView.invalidate()
    }

    override fun scheduleDrawable(@NonNull drawable: Drawable, @NonNull runnable: Runnable, l: Long) {

    }

    override fun unscheduleDrawable(@NonNull drawable: Drawable, @NonNull runnable: Runnable) {

    }

    fun clear() {
        for (target in imageTargets) {
            glide.clear(target)
        }
    }

    private fun createGlideRequest(glide: GlideRequests): RequestBuilder<Drawable> {
        return glide.asDrawable()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

    private inner class WrapperTarget(width: Int, height: Int) : SimpleTarget<Drawable>(width, height) {

        /**
         * Workaround because the AppCompat DrawableWrapper doesn't support null drawable as the API23 version does
         */
        private val nullObject = ColorDrawable(Color.TRANSPARENT)
        private val progressDrawable = CircularProgressDrawable(context)
        private val placeholderDrawable = ColorDrawable(ContextCompat.getColor(context, R.color.grey300))
        private val wrapper = DrawableWrapper(null/* temporarily null until a setDrawable call*/)
        private val layerDrawable: LayerDrawable

        val lazyDrawable: Drawable
            get() = wrapper

        init {
            setDrawable(null)
            // set wrapper bounds to fix the height of the view, TextViews don't like ImageSpans changing dimensions
            wrapper.setBounds(0, 0, width, height)

            val drawables = arrayOf(placeholderDrawable, progressDrawable)
            layerDrawable = LayerDrawable(drawables)
            wrapper.wrappedDrawable = layerDrawable
            progressDrawable.setStyle(CircularProgressDrawable.DEFAULT)
            val typedValue = TypedValue()
            targetView.context?.theme?.resolveAttribute(R.attr.colorSecondary, typedValue, true)
            progressDrawable.setColorSchemeColors(typedValue.data)
        }

        override fun onLoadStarted(placeholder: Drawable?) {
            progressDrawable.start()
            setDrawable(layerDrawable)
        }

        override fun onLoadFailed(@Nullable errorDrawable: Drawable?) {
            progressDrawable.stop()
        }

        override fun onResourceReady(@NonNull resource: Drawable, @Nullable transition: Transition<in Drawable>?) {
            progressDrawable.stop()
            setDrawable(resource)
            if (resource is Animatable)
                (resource as Animatable).start()
        }

        override fun onLoadCleared(placeholder: Drawable?) {
            progressDrawable.stop()
        }

        private fun setDrawable(drawable: Drawable?) {
            val _drawable = drawable ?: nullObject
            _drawable.bounds = calcBounds(_drawable, Gravity.CENTER_HORIZONTAL)
            wrapper.wrappedDrawable = _drawable
            // invalidate wrapper drawable so it re-draws itself and displays the new wrapped drawable
            wrapper.invalidateSelf()
        }

        /**
         * Align drawable in wrapper in case the image is smaller than the target size.
         */
        private fun calcBounds(drawable: Drawable, gravity: Int): Rect {
            val bounds = Rect()
            var w = drawable.intrinsicWidth
            var h = drawable.intrinsicHeight
            val container = wrapper.bounds
            if (w == -1 && h == -1) {
                w = container.width()
                h = container.height()
            }
            Gravity.apply(gravity, w, h, container, bounds)
            return bounds
        }
    }
}
