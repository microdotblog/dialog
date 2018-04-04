package com.dialogapp.dialog.di.viewmodule;

import com.dialogapp.dialog.ui.MainActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class MainActivityModule {
    @ContributesAndroidInjector(modules = ListFragmentModule.class)
    abstract MainActivity contributeMainActivity();
}
