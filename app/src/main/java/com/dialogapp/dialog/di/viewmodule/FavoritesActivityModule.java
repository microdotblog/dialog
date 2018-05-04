package com.dialogapp.dialog.di.viewmodule;

import com.dialogapp.dialog.ui.favorites.FavoritesActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FavoritesActivityModule {
    @ContributesAndroidInjector(modules = FavoritesActivityFragmentsModule.class)
    abstract FavoritesActivity contributesFavoriteActivity();
}
