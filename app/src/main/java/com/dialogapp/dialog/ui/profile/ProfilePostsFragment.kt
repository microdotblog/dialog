package com.dialogapp.dialog.ui.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dialogapp.dialog.databinding.FragmentListBinding
import com.dialogapp.dialog.di.Injector
import com.dialogapp.dialog.ui.common.BaseListFragment
import com.dialogapp.dialog.ui.common.EndpointArgs
import com.dialogapp.dialog.ui.util.autoCleared
import com.google.android.material.chip.Chip
import timber.log.Timber

class ProfilePostsFragment : BaseListFragment() {

    private var binding by autoCleared<FragmentListBinding>()

    companion object {
        private const val USERNAME = "username"

        fun newInstance(username: String): ProfilePostsFragment {
            val profilePostsFragment = ProfilePostsFragment()
            profilePostsFragment.arguments = bundleOf(USERNAME to username)
            return profilePostsFragment
        }
    }

    private lateinit var profileSharedViewModel: ProfileSharedViewModel

    override fun onAttach(context: Context?) {
        viewModelFactory = Injector.get().viewModelFactory()
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val username = arguments?.getString(USERNAME)
        if (username != null) {
            baseListViewModel.showEndpoint(EndpointArgs(username, null))
        }

        profileSharedViewModel = ViewModelProviders.of(parentFragment!!).get(ProfileSharedViewModel::class.java)
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

    override fun getRecyclerView(): RecyclerView = binding.recyclerPosts

    override fun getSwipeRefreshLayout(): SwipeRefreshLayout = binding.swipeRefresh

    override fun getNotificationChip(): Chip = binding.chipNotification
}
