package com.dialogapp.dialog.ui.imageviewer;

import android.os.Bundle;

import com.dialogapp.dialog.R;
import com.dialogapp.dialog.ui.base.BaseInjectableActivity;

public class ImageViewerActivity extends BaseInjectableActivity {
    public static final String EXTRA_IMAGE_URL = ImageViewerActivity.class.getName() + ".EXTRA_IMAGE_URL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);

        String url = getIntent().getStringExtra(EXTRA_IMAGE_URL);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_common, ImageViewerFragment.newInstance(url))
                .commit();
    }
}
