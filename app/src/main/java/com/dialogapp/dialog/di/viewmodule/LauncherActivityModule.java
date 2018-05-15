package com.dialogapp.dialog.di.viewmodule;

import com.dialogapp.dialog.LauncherActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class LauncherActivityModule {
    @ContributesAndroidInjector
    abstract LauncherActivity contributesLauncherActivity();
}
