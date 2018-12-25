package com.dialogapp.dialog.ui.profile

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.dialogapp.dialog.GlideApp
import com.dialogapp.dialog.di.Injector
import com.dialogapp.dialog.ui.common.BaseListFragment
import com.dialogapp.dialog.vo.Status
import timber.log.Timber

class FollowingFragment : BaseListFragment() {

    private lateinit var viewModel: FollowingViewModel
    private lateinit var profileSharedViewModel: ProfileSharedViewModel
    private lateinit var followingAdapter: FollowingAdapter

    companion object {
        private const val USERNAME = "username"
        private const val IS_SELF = "is_self"

        fun newInstance(username: String, isSelf: Boolean): FollowingFragment {
            val followingFragment = FollowingFragment()
            followingFragment.arguments = bundleOf(USERNAME to username, IS_SELF to isSelf)
            return followingFragment
        }
    }

    override fun onAttach(context: Context?) {
        viewModelFactory = Injector.get().viewModelFactory()
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val username = arguments?.getString(USERNAME)!!
        val isSelf = arguments?.getBoolean(IS_SELF)!!
        viewModel.showEndpoint(FollowingEndpointArgs(username, isSelf))
    }

    override fun initViewModel() {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(FollowingViewModel::class.java)
        profileSharedViewModel = ViewModelProviders.of(parentFragment!!).get(ProfileSharedViewModel::class.java)
    }

    override fun onSwipeRefresh() {
        viewModel.refresh()
    }

    override fun observe() {
        viewModel.endpointResult.observe(viewLifecycleOwner, Observer {
            val result = it ?: return@Observer

            binding.swipeRefresh.isRefreshing = result.status == Status.LOADING
            if (result.data != null) {
                followingAdapter.submitList(result.data)
            } else if (result.status == Status.ERROR) {
                followingAdapter.submitList(emptyList())
            }
        })
    }

    override fun onProfileClicked(username: String) {
        if (profileSharedViewModel.isProfileCurrentlyShown(username)) {
            Timber.d("Profile requested is currently on top of stack")
            return
        }
        super.onProfileClicked(username)
    }

    override fun initRecyclerAdapter() {
        val glide = GlideApp.with(this)
        followingAdapter = FollowingAdapter(glide, this)
        binding.recyclerPosts.adapter = followingAdapter
    }
}