package com.dialogapp.dialog.di;

import com.dialogapp.dialog.ui.mainscreen.mentions.MentionsFragment;
import com.dialogapp.dialog.ui.mainscreen.timeline.TimelineFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class MainActivityFragmentsModule {
    @ContributesAndroidInjector
    abstract TimelineFragment contributeTimelineFragment();

    @ContributesAndroidInjector
    abstract MentionsFragment contributeMentionsFragment();
}
