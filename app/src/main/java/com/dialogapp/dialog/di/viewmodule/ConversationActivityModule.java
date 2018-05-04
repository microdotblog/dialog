package com.dialogapp.dialog.di.viewmodule;

import com.dialogapp.dialog.ui.conversation.ConversationActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ConversationActivityModule {
    @ContributesAndroidInjector(modules = ConversationActivityFragmentsModule.class)
    abstract ConversationActivity contributesConversationActivity();
}
