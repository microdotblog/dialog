package com.dialogapp.dialog.ui.mainscreen;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.dialogapp.dialog.ui.common.ListFragment;

public class MainFragmentPagerAdapter extends FragmentPagerAdapter {

    private final String[] TAB_TITLES = new String[]{"TIMELINE", "MENTIONS", "DISCOVER"};

    public MainFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return ListFragment.newInstance(ListFragment.TIMELINE, null);
            case 1:
                return ListFragment.newInstance(ListFragment.MENTIONS, null);
            case 2:
                return ListFragment.newInstance(ListFragment.DISCOVER, null);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return TAB_TITLES[position];
    }
}
