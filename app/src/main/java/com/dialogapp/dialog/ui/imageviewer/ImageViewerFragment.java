package com.dialogapp.dialog.ui.imageviewer;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.dialogapp.dialog.R;
import com.dialogapp.dialog.di.Injectable;
import com.github.chrisbanes.photoview.PhotoView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ImageViewerFragment extends Fragment implements Injectable {
    private static final String ARG_IMAGE_URL = ImageViewerFragment.class.getName() + ".ARG_IMAGE_URL";

    private String imageUrl;
    private Unbinder unbinder;

    @BindView(R.id.frame_image_viewer)
    FrameLayout frameLayout;

    @BindView(R.id.image_post)
    PhotoView imageView;

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
        }
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
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setIndeterminate(true);

            Glide.with(this)
                    .load(imageUrl)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                    Target<Drawable> target, boolean isFirstResource) {
                            progressBar.setIndeterminate(false);
                            Snackbar.make(frameLayout, "Could not load image", Snackbar.LENGTH_INDEFINITE)
                                    .show();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target,
                                                       DataSource dataSource, boolean isFirstResource) {
                            progressBar.setIndeterminate(false);
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(imageView);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
