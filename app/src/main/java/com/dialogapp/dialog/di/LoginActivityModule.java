package com.dialogapp.dialog.di;

import com.dialogapp.dialog.ui.loginscreen.LoginActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class LoginActivityModule {
    @ContributesAndroidInjector
    abstract LoginActivity contributesLoginActivity();
}
