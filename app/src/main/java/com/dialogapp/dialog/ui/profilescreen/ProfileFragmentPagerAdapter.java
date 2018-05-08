package com.dialogapp.dialog.ui.profilescreen;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class ProfileFragmentPagerAdapter extends FragmentStatePagerAdapter {

    private final String[] TAB_TITLES = new String[]{"POSTS", "FOLLOWING"};
    private ArrayList<WeakReference<Fragment>> fragments = new ArrayList<>();

    public ProfileFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFragment(Fragment fragment) {
        fragments.add(new WeakReference<>(fragment));
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position).get();
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return TAB_TITLES[position];
    }
}
