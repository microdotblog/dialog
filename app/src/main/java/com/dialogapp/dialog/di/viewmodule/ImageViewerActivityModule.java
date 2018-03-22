package com.dialogapp.dialog.di.viewmodule;

import com.dialogapp.dialog.ui.imageviewer.ImageViewerActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ImageViewerActivityModule {
    @ContributesAndroidInjector(modules = ImageViewerFragmentsModule.class)
    public abstract ImageViewerActivity contributesImageViewerActivity();
}
