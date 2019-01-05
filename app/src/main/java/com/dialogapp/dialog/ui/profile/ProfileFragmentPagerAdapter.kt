package com.dialogapp.dialog.ui.profile

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.dialogapp.dialog.ui.profile.following.FollowingFragment

class ProfileFragmentPagerAdapter(fm: FragmentManager, private val username: String)
    : FragmentPagerAdapter(fm) {

    private val TAB_TITLES = arrayOf("POSTS", "FOLLOWING")

    override fun getCount(): Int {
        return TAB_TITLES.size
    }

    override fun getItem(position: Int): Fragment? {
        return when (position) {
            0 -> ProfilePostsFragment.newInstance(username)
            1 -> FollowingFragment.newInstance(username, false)
            else -> null
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return TAB_TITLES[position]
    }
}