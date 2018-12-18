package com.dialogapp.dialog.ui.profile

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.dialogapp.dialog.GlideApp
import com.dialogapp.dialog.R
import com.dialogapp.dialog.databinding.FragmentListBinding
import com.dialogapp.dialog.di.Injector
import com.dialogapp.dialog.ui.common.BasePostFragment
import com.dialogapp.dialog.ui.util.autoCleared
import com.dialogapp.dialog.vo.Status

class FollowingFragment : BasePostFragment() {

    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var viewModel: FollowingViewModel

    private var binding by autoCleared<FragmentListBinding>()

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
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

        binding.setLifecycleOwner(viewLifecycleOwner)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(FollowingViewModel::class.java)
        initSwipeToRefresh()
        initAdapter()
        val username = arguments?.getString(USERNAME)!!
        val isSelf = arguments?.getBoolean(IS_SELF)!!
        viewModel.showEndpoint(FollowingEndpointArgs(username, isSelf))
    }

    private fun initSwipeToRefresh() {
        val typedValue = TypedValue()
        context?.theme?.resolveAttribute(R.attr.colorSecondary, typedValue, true)
        binding.swipeRefresh.setColorSchemeColors(typedValue.data)
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
        }
    }

    private fun initAdapter() {
        val glide = GlideApp.with(this)
        val adapter = FollowingAdapter(glide, this)
        binding.recyclerPosts.adapter = adapter
        viewModel.endpointResult.observe(viewLifecycleOwner, Observer {
            val result = it ?: return@Observer

            binding.swipeRefresh.isRefreshing = result.status == Status.LOADING
            adapter.submitList(result.data)
        })
    }
}