package com.dialogapp.dialog.di.viewmodule;

import com.dialogapp.dialog.ui.favorites.FavoritesActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FavoritesActivityModule {
    @ContributesAndroidInjector(modules = FavoritesActivityFragmentModule.class)
    public abstract FavoritesActivity contributesFavoriteActivity();
}
