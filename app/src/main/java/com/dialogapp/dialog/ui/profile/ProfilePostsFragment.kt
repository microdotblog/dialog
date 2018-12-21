package com.dialogapp.dialog.ui.profile

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.dialogapp.dialog.di.Injector
import com.dialogapp.dialog.ui.common.BaseListFragment
import com.dialogapp.dialog.ui.common.EndpointArgs
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

    private val profileSharedViewModel: ProfileSharedViewModel by lazy {
        ViewModelProviders.of(parentFragment!!).get(ProfileSharedViewModel::class.java)
    }

    override fun onAttach(context: Context?) {
        viewModelFactory = Injector.get().viewModelFactory()
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val username = arguments?.getString(USERNAME)
        if (username != null) {
            baseListViewModel.showEndpoint(EndpointArgs(username, null))
        }

        baseListViewModel.endpointResult.observe(viewLifecycleOwner, Observer {
            profileSharedViewModel.setEndpointData(it?.data?.endpointData)
        })
    }

    override fun onProfileClicked(username: String) {
        if (profileSharedViewModel.isProfileCurrentlyShown(username)) {
            Timber.d("Profile requested is currently on top of stack")
            return
        }
        super.onProfileClicked(username)
    }
}
