package com.dialogapp.dialog.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
import androidx.preference.PreferenceManager;
import android.text.Html;
import android.view.Gravity;
import android.widget.TextView;

import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.dialogapp.dialog.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import static com.bumptech.glide.request.RequestOptions.centerCropTransform;
import static com.bumptech.glide.request.RequestOptions.diskCacheStrategyOf;

/**
 * Custom ImageGetter using Glide 4
 * <p>
 * Adapted from https://github.com/TWiStErRob/glide-support/commit/c3de297d0daec8d2650a3a640c0404c35b937a30
 */
public class GlideImageGetter implements Html.ImageGetter, Drawable.Callback {
    private static final int DEFAULT_WIDTH_PX = 48;
    private static final int DEFAULT_HEIGHT_PX = 48;
    private static final int DEFAULT_IMAGE_HEIGHT_DP = 192;

    // values from post_item layout
    private static final int CONTENT_MARGIN_START_DP = 66;
    private static final int CONTENT_MARGIN_END_DP = 16;

    private final RequestManager manager;
    private final boolean isConnectedToWifi;
    private RequestBuilder<Drawable> glide;
    private TextView targetView;
    private Context context;
    private Queue<Boolean> imageQueue;
    private int shouldLoadImages;
    private int calculatedWidthPx;
    private int calculatedHeightPx;
    private List<Target> imageTargets = new ArrayList<>();

    public GlideImageGetter(RequestManager glide, TextView targetView, Queue<Boolean> imageQueue) {
        this.manager = glide;
        this.targetView = targetView;
        this.context = targetView.getContext().getApplicationContext();
        this.imageQueue = imageQueue;
        this.isConnectedToWifi = NetworkUtil.getConnectivityStatus(context) == NetworkUtil.TYPE_WIFI;

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        this.shouldLoadImages = Integer.parseInt(sharedPref.getString(context.getString(R.string.pref_preloadImages), "0"));

        this.calculatedHeightPx = dpToPx(DEFAULT_IMAGE_HEIGHT_DP);
        int diffPx = dpToPx(CONTENT_MARGIN_START_DP + CONTENT_MARGIN_END_DP);
        this.calculatedWidthPx = Resources.getSystem().getDisplayMetrics().widthPixels - diffPx;

        this.glide = createGlideRequest(glide);
        targetView.setTag(this);
    }

    public void clear() {
        for (Target target : imageTargets) {
            manager.clear(target);
        }
    }

    public static void clear(TextView view) {
        view.setText(null);
        Object tag = view.getTag();
        if (tag instanceof GlideImageGetter) {
            ((GlideImageGetter) tag).clear();
            view.setTag(null);
        }
    }

    @Override
    public Drawable getDrawable(String url) {
        int drawableWidth;
        int drawableHeight;

        boolean smallImage = imageQueue.remove();
        if (smallImage) {
            // image at current position in the text is small-sized
            drawableWidth = DEFAULT_WIDTH_PX;
            drawableHeight = DEFAULT_HEIGHT_PX;
        } else {
            // image at current position in the text is a large-sized
            drawableWidth = calculatedWidthPx;
            drawableHeight = calculatedHeightPx;
        }
        // set up target for this Image inside the TextView
        WrapperTarget imageTarget = new WrapperTarget(drawableWidth, drawableHeight);
        Drawable asyncWrapper = imageTarget.getLazyDrawable();
        // listen for Drawable's request for invalidation
        asyncWrapper.setCallback(this);

        // start Glide's async load
        if (smallImage || shouldLoadImages == 0 || (shouldLoadImages == 1 && isConnectedToWifi)) {
            glide.load(url).into(imageTarget);
        } else {
            glide.load(url)
                    .apply(new RequestOptions().onlyRetrieveFromCache(true))
                    .into(imageTarget);
        }
        // save target for clearing it later
        imageTargets.add(imageTarget);
        return asyncWrapper;
    }

    @Override
    public void invalidateDrawable(@NonNull Drawable drawable) {
        targetView.invalidate();
    }

    @Override
    public void scheduleDrawable(@NonNull Drawable drawable, @NonNull Runnable runnable, long l) {

    }

    @Override
    public void unscheduleDrawable(@NonNull Drawable drawable, @NonNull Runnable runnable) {

    }

    private RequestBuilder<Drawable> createGlideRequest(RequestManager glide) {
        return glide.asDrawable()
                .apply(centerCropTransform())
                .apply(diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC));
    }

    private int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    private class WrapperTarget extends SimpleTarget<Drawable> {
        /**
         * Workaround because the AppCompat DrawableWrapper doesn't support null drawable as the API23 version does
         */
        private final Drawable nullObject = new ColorDrawable(Color.TRANSPARENT);
        private final CircularProgressDrawable progressDrawable = new CircularProgressDrawable(context);
        private final ColorDrawable placeholderDrawable = new ColorDrawable(ContextCompat.getColor(context, R.color.grey300));
        private final DrawableWrapper wrapper = new DrawableWrapper(null/* temporarily null until a setDrawable call*/);
        private LayerDrawable layerDrawable;

        public WrapperTarget(int width, int height) {
            super(width, height);
            setDrawable(null);
            // set wrapper bounds to fix the height of the view, TextViews don't like ImageSpans changing dimensions
            wrapper.setBounds(0, 0, width, height);

            Drawable[] drawables = new Drawable[] {placeholderDrawable, progressDrawable};
            layerDrawable = new LayerDrawable(drawables);
            wrapper.setWrappedDrawable(layerDrawable);
            progressDrawable.setStyle(CircularProgressDrawable.DEFAULT);
            progressDrawable.setColorSchemeColors(ContextCompat.getColor(context, R.color.reda200));
        }

        public Drawable getLazyDrawable() {
            return wrapper;
        }

        @Override
        public void onLoadStarted(Drawable placeholder) {
            progressDrawable.start();
            setDrawable(layerDrawable);
        }

        @Override
        public void onLoadFailed(@Nullable Drawable errorDrawable) {
            progressDrawable.stop();
        }

        @Override
        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
            progressDrawable.stop();
            setDrawable(resource);
            if (resource instanceof Animatable)
                ((Animatable) resource).start();
        }

        @Override
        public void onLoadCleared(Drawable placeholder) {
            progressDrawable.stop();
        }

        private void setDrawable(Drawable drawable) {
            if (drawable == null) {
                drawable = nullObject;
            }
            drawable.setBounds(calcBounds(drawable, Gravity.CENTER_HORIZONTAL));
            wrapper.setWrappedDrawable(drawable);
            // invalidate wrapper drawable so it re-draws itself and displays the new wrapped drawable
            wrapper.invalidateSelf();
        }

        /**
         * Align drawable in wrapper in case the image is smaller than the target size.
         */
        private Rect calcBounds(Drawable drawable, int gravity) {
            Rect bounds = new Rect();
            int w = drawable.getIntrinsicWidth();
            int h = drawable.getIntrinsicHeight();
            Rect container = wrapper.getBounds();
            if (w == -1 && h == -1) {
                w = container.width();
                h = container.height();
            }
            Gravity.apply(gravity, w, h, container, bounds);
            return bounds;
        }
    }
}
