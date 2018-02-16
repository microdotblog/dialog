package com.dialogapp.dialog.di.viewmodule;

import com.dialogapp.dialog.ui.mainscreen.MainActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class MainActivityModule {
    @ContributesAndroidInjector(modules = MainActivityFragmentsModule.class)
    abstract MainActivity contributeMainActivity();
}
