package com.dialogapp.dialog.ui.profile

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.dialogapp.dialog.di.Injector
import com.dialogapp.dialog.ui.common.BaseListFragment
import com.dialogapp.dialog.ui.common.EndpointArgs
import com.dialogapp.dialog.ui.common.PostClickedListener
import timber.log.Timber

class ProfilePostsFragment : BaseListFragment() {

    companion object {
        private const val USERNAME = "username"

        fun newInstance(username: String): ProfilePostsFragment {
            val profilePostsFragment = ProfilePostsFragment()
            profilePostsFragment.arguments = bundleOf(USERNAME to username)
            return profilePostsFragment
        }
    }

    override fun onAttach(context: Context?) {
        viewModelFactory = Injector.get().viewModelFactory()
        super.onAttach(context)
    }

    // https://stackoverflow.com/a/22673871
    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        return if (!enter && parentFragment != null) {
            val dummyAnimation = AlphaAnimation(1f, 1f)
            dummyAnimation.duration = android.R.integer.config_mediumAnimTime.toLong()
            dummyAnimation
        } else super.onCreateAnimation(transit, enter, nextAnim)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val profileFragment = parentFragment as ProfileFragment

        val username = arguments?.getString(USERNAME)
        if (username != null) {
            baseListViewModel.showEndpoint(EndpointArgs(username, null))
        }

        baseListViewModel.endpointResult.observe(viewLifecycleOwner, Observer {
            val result = it ?: return@Observer

            profileFragment.setEndpointData(result.data?.endpointData)
        })
    }

    override fun onProfileClicked(username: String, postClickedListener: PostClickedListener) {
        with((postClickedListener as Fragment).parentFragment as ProfileFragment) {
            if (this.isUserCurrentProfile(username)) {
                Timber.d("Profile requested is currently on top of stack")
                return
            }
            super.onProfileClicked(username, postClickedListener)
        }
    }
}
