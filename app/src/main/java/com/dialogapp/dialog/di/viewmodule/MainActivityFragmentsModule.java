package com.dialogapp.dialog.di.viewmodule;

import com.dialogapp.dialog.ui.mainscreen.DiscoverFragment;
import com.dialogapp.dialog.ui.mainscreen.ListFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
abstract class MainActivityFragmentsModule {
    @ContributesAndroidInjector
    abstract ListFragment contributesListFragment();

    @ContributesAndroidInjector
    abstract DiscoverFragment contributesDiscoverFragment();
}
