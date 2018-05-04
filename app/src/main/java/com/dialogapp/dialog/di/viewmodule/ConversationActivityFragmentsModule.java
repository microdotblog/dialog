package com.dialogapp.dialog.di.viewmodule;

import com.dialogapp.dialog.ui.conversation.ConversationFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
abstract class ConversationActivityFragmentsModule {
    @ContributesAndroidInjector
    abstract ConversationFragment contributesConversationFragment();
}
