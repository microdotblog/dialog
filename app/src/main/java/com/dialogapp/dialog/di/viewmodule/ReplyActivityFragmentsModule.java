package com.dialogapp.dialog.di.viewmodule;

import com.dialogapp.dialog.ui.common.ReplyFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
abstract class ReplyActivityFragmentsModule {
    @ContributesAndroidInjector
    abstract ReplyFragment contributesReplyFragment();
}
