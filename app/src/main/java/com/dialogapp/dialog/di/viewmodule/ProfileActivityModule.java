package com.dialogapp.dialog.di.viewmodule;

import com.dialogapp.dialog.ui.profilescreen.ProfileActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ProfileActivityModule {
    @ContributesAndroidInjector(modules = ProfileActivityFragmentsModule.class)
    abstract ProfileActivity contributesProfileActivity();
}
