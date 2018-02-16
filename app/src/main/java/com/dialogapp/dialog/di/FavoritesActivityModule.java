package com.dialogapp.dialog.di;

import com.dialogapp.dialog.ui.favorites.FavoritesActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FavoritesActivityModule {
    @ContributesAndroidInjector
    public abstract FavoritesActivity contributesFavoriteActivity();
}
