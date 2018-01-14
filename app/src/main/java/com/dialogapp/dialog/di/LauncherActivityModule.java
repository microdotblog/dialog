package com.dialogapp.dialog.di;

import com.dialogapp.dialog.ui.LauncherActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class LauncherActivityModule {
    @ContributesAndroidInjector
    abstract LauncherActivity contributesLauncherActivity();
}
