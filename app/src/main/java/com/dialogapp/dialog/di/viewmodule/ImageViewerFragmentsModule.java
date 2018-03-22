package com.dialogapp.dialog.di.viewmodule;

import com.dialogapp.dialog.ui.imageviewer.ImageViewerFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
abstract class ImageViewerFragmentsModule {
    @ContributesAndroidInjector
    abstract ImageViewerFragment contributesImageViewerFragment();
}
