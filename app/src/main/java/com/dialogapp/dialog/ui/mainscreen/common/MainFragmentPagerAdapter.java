package com.dialogapp.dialog.ui.mainscreen.common;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.dialogapp.dialog.ui.mainscreen.mentions.MentionsFragment;
import com.dialogapp.dialog.ui.mainscreen.timeline.TimelineFragment;

public class MainFragmentPagerAdapter extends FragmentPagerAdapter {

    private final int PAGE_COUNT = 2;
    private final String[] TAB_TITLES = new String[]{"MENTIONS", "TIMELINE"};

    public MainFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new MentionsFragment();
            case 1:
                return new TimelineFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return TAB_TITLES[position];
    }
}
