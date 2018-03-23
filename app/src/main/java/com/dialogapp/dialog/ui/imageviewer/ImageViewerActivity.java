package com.dialogapp.dialog.ui.imageviewer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.dialogapp.dialog.R;

import javax.inject.Inject;

import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

public class ImageViewerActivity extends AppCompatActivity implements HasSupportFragmentInjector {
    public static final String EXTRA_IMAGE_URL = ImageViewerActivity.class.getName() + ".EXTRA_IMAGE_URL";

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);

        String url = getIntent().getStringExtra(EXTRA_IMAGE_URL);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_common, ImageViewerFragment.newInstance(url))
                .commit();
    }

    @Override
    public DispatchingAndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
    }
}
