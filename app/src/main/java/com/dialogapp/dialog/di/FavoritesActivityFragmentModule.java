package com.dialogapp.dialog.di;

import com.dialogapp.dialog.ui.favorites.FavoritesFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
abstract class FavoritesActivityFragmentModule {
    @ContributesAndroidInjector
    abstract FavoritesFragment contributesFavoritesFragment();
}
