package com.dialogapp.dialog.ui.imageviewer;

import android.os.Bundle;
import android.view.WindowManager;

import com.dialogapp.dialog.R;
import com.dialogapp.dialog.ui.base.BaseInjectableActivity;

public class ImageViewerActivity extends BaseInjectableActivity {
    public static final String EXTRA_IMAGE_URL = ImageViewerActivity.class.getName() + ".EXTRA_IMAGE_URL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_blank);

        String url = getIntent().getStringExtra(EXTRA_IMAGE_URL);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_blank, ImageViewerFragment.newInstance(url))
                .commit();
    }
}
