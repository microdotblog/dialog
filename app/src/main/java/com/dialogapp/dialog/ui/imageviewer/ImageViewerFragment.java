package com.dialogapp.dialog.ui.imageviewer;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.dialogapp.dialog.R;
import com.dialogapp.dialog.di.Injectable;
import com.github.piasy.biv.BigImageViewer;
import com.github.piasy.biv.indicator.progresspie.ProgressPieIndicator;
import com.github.piasy.biv.loader.glide.GlideImageLoader;
import com.github.piasy.biv.view.BigImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ImageViewerFragment extends Fragment implements Injectable {
    private static final String ARG_IMAGE_URL = ImageViewerFragment.class.getName() + ".ARG_IMAGE_URL";

    private String imageUrl;
    private Unbinder unbinder;
    private boolean isGif;

    @BindView(R.id.frame_image_viewer)
    FrameLayout frameLayout;

    @BindView(R.id.image_post)
    BigImageView imagePost;

    @BindView(R.id.image_post_gif)
    ImageView imagePostGif;

    @BindView(R.id.progressBar_image_viewer)
    ProgressBar progressBar;

    public static ImageViewerFragment newInstance(String url) {
        ImageViewerFragment fragment = new ImageViewerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_IMAGE_URL, url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imageUrl = getArguments().getString(ARG_IMAGE_URL);
            if (imageUrl != null && imageUrl.endsWith(".gif"))
                isGif = true;
        }

        BigImageViewer.initialize(GlideImageLoader.with(this.getContext().getApplicationContext()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_viewer, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (imageUrl == null) {
            Snackbar.make(frameLayout, "Invalid image url", Snackbar.LENGTH_INDEFINITE)
                    .show();
        } else {
            if (isGif) {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setIndeterminate(true);
                RequestOptions requestOptions = new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .error(R.drawable.ic_broken_image_white_24dp);

                imagePost.setVisibility(View.GONE);
                imagePostGif.setVisibility(View.VISIBLE);
                Glide.with(this)
                        .asGif()
                        .apply(requestOptions)
                        .load(imageUrl)
                        .listener(new RequestListener<GifDrawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                        Target<GifDrawable> target, boolean isFirstResource) {
                                progressBar.setIndeterminate(false);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GifDrawable resource, Object model,
                                                           Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                                progressBar.setIndeterminate(false);
                                progressBar.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(imagePostGif);
            } else {
                imagePost.setProgressIndicator(new ProgressPieIndicator());
                imagePost.showImage(Uri.parse(imageUrl));
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
