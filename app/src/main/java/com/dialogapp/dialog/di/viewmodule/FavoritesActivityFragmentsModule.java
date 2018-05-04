package com.dialogapp.dialog.di.viewmodule;

import com.dialogapp.dialog.ui.favorites.FavoritesFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
abstract class FavoritesActivityFragmentsModule {
    @ContributesAndroidInjector
    abstract FavoritesFragment contributesFavoritesFragment();
}