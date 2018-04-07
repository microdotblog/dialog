package com.dialogapp.dialog.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.CircularProgressDrawable;
import android.text.Html;
import android.view.Gravity;
import android.widget.TextView;

import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.dialogapp.dialog.R;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Custom ImageGetter using Glide 4
 * <p>
 * Adapted from https://github.com/TWiStErRob/glide-support/commit/c3de297d0daec8d2650a3a640c0404c35b937a30
 */
public class GlideImageGetter implements Html.ImageGetter, Drawable.Callback {

    private final RequestManager manager;
    private final RequestOptions requestOptions;
    private RequestBuilder<Drawable> glide;
    private TextView targetView;
    private Context context;
    private int size;
    private List<Target> imageTargets = new ArrayList<>();

    public GlideImageGetter(RequestManager glide, TextView targetView, int size) {
        this.manager = glide;
        this.targetView = targetView;
        this.context = targetView.getContext().getApplicationContext();
        this.size = size;

        this.requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .centerCrop();
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
        // set up target for this Image inside the TextView
        WrapperTarget imageTarget = new WrapperTarget(size);
        Drawable asyncWrapper = imageTarget.getLazyDrawable();
        // listen for Drawable's request for invalidation
        asyncWrapper.setCallback(this);

        // start Glide's async load
        glide.load(url).into(imageTarget);
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
        return glide.asDrawable().apply(requestOptions)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                Target<Drawable> target, boolean isFirstResource) {
                        Timber.e(e);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model,
                                                   Target<Drawable> target, DataSource dataSource,
                                                   boolean isFirstResource) {
                        return false;
                    }
                });
    }

    private class WrapperTarget extends SimpleTarget<Drawable> {
        /**
         * Workaround because the AppCompat DrawableWrapper doesn't support null drawable as the API23 version does
         */
        private final Drawable nullObject = new ColorDrawable(Color.TRANSPARENT);
        private final CircularProgressDrawable progressDrawable = new CircularProgressDrawable(context);
        private final DrawableWrapper wrapper = new DrawableWrapper(null/* temporarily null until a setDrawable call*/);

        public WrapperTarget(int size) {
            super(size, size);
            setDrawable(null);
            // set wrapper bounds to fix the size of the view, TextViews don't like ImageSpans changing dimensions
            wrapper.setBounds(0, 0, size, size);

            progressDrawable.setStyle(CircularProgressDrawable.DEFAULT);
        }

        public Drawable getLazyDrawable() {
            return wrapper;
        }

        @Override
        public void onLoadStarted(Drawable placeholder) {
            Timber.i("Load started : %s", this);
            placeholder = progressDrawable;
            progressDrawable.start();
            setDrawable(placeholder);
        }

        @Override
        public void onLoadFailed(@Nullable Drawable errorDrawable) {
            Timber.i("Load Failed : %s", this);
            progressDrawable.stop();
            errorDrawable = ContextCompat.getDrawable(context, R.drawable.ic_broken_image_black_24dp);
            setDrawable(errorDrawable);
        }

        @Override
        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
            Timber.i("Resource Ready : %s", this);
            progressDrawable.stop();
            setDrawable(resource);
        }

        @Override
        public void onLoadCleared(Drawable placeholder) {
            Timber.i("Load cleared : %s", this);
            progressDrawable.stop();
            setDrawable(placeholder);
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
