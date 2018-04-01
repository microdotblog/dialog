package com.dialogapp.dialog.di.viewmodule;

import com.dialogapp.dialog.ui.loginscreen.LoginFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
abstract class LoginFragmentModule {
    @ContributesAndroidInjector
    abstract LoginFragment contributesLoginFragment();
}
