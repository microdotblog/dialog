package com.dialogapp.dialog.di.viewmodule;

import com.dialogapp.dialog.ui.common.ReplyActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ReplyActivityModule {
    @ContributesAndroidInjector(modules = ReplyActivityFragmentsModule.class)
    abstract ReplyActivity contributesReplyActivity();
}
