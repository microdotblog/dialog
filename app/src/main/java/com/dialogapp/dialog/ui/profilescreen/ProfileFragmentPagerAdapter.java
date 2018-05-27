package com.dialogapp.dialog.ui.profilescreen;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.dialogapp.dialog.ui.profilescreen.favorites.FavoritesFragment;
import com.dialogapp.dialog.ui.profilescreen.following.FollowingFragment;

public class ProfileFragmentPagerAdapter extends FragmentPagerAdapter {

    private final String[] TAB_TITLES = new String[]{"POSTS", "FAVORITES", "FOLLOWING"};
    private String username;

    ProfileFragmentPagerAdapter(FragmentManager fm, String username) {
        super(fm);
        this.username = username;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return ProfileFragment.newInstance(username);
            case 1:
                return new FavoritesFragment();
            case 2:
                return FollowingFragment.newInstance(username);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return TAB_TITLES.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return TAB_TITLES[position];
    }
}
