package com.dialogapp.dialog.di;

import com.dialogapp.dialog.ui.mainscreen.MainActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class MainActivityModule {
    @ContributesAndroidInjector
    abstract MainActivity contributeMainActivity();
}
