package com.dialogapp.dialog.di.viewmodule;

import com.dialogapp.dialog.ui.profilescreen.favorites.FavoritesFragment;
import com.dialogapp.dialog.ui.profilescreen.following.FollowingFragment;
import com.dialogapp.dialog.ui.profilescreen.ProfileFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
abstract class ProfileActivityFragmentsModule {
    @ContributesAndroidInjector
    abstract ProfileFragment contributesProfileFragment();

    @ContributesAndroidInjector
    abstract FavoritesFragment contributesFavoritesFragment();

    @ContributesAndroidInjector
    abstract FollowingFragment contributesFollowingFragment();
}
