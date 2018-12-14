package com.dialogapp.dialog.ui.common

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import com.dialogapp.dialog.GlideApp
import com.dialogapp.dialog.R
import com.dialogapp.dialog.databinding.FragmentListBinding
import com.dialogapp.dialog.model.Post
import com.dialogapp.dialog.repository.NetworkState
import com.dialogapp.dialog.ui.util.autoCleared
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

abstract class BasePagedListFragment : BasePostFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var basePagedListViewModel: BasePagedListViewModel

    private var binding by autoCleared<FragmentListBinding>()

    companion object {
        private val TIMEOUT: Long = TimeUnit.MINUTES.toMillis(15)

        fun shouldRefresh(lastFetchTimestamp: Long): Boolean {
            val now = System.currentTimeMillis()
            return (now - lastFetchTimestamp > TIMEOUT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.setLifecycleOwner(viewLifecycleOwner)
        basePagedListViewModel = ViewModelProviders.of(this, viewModelFactory).get(BasePagedListViewModel::class.java)

        initAdapter()
        initSwipeToRefresh()
    }

    private fun initSwipeToRefresh() {
        val typedValue = TypedValue()
        activity?.theme?.resolveAttribute(R.attr.colorSecondary, typedValue, true)
        binding.swipeRefresh.setColorSchemeColors(typedValue.data)
        basePagedListViewModel.refreshState.observe(this, Observer {
            binding.swipeRefresh.isRefreshing = it == NetworkState.LOADING
        })
        binding.swipeRefresh.setOnRefreshListener {
            basePagedListViewModel.refresh()
        }
    }

    private fun initAdapter() {
        val glide = GlideApp.with(this)
        val adapter = PagedPostsAdapter(glide, this) {
            basePagedListViewModel.retry()
        }
        binding.recyclerPosts.adapter = adapter
        basePagedListViewModel.posts.observe(viewLifecycleOwner, Observer<PagedList<Post>> {
            adapter.submitList(it)
        })
        basePagedListViewModel.endpointData.observe(viewLifecycleOwner, Observer {
            if (it != null && BasePagedListFragment.shouldRefresh(it.lastFetched)) {
                Timber.i("Stale data, refreshing endpoint automatically - %s", basePagedListViewModel.currentEndpoint())
                basePagedListViewModel.refresh()
            }
        })
        basePagedListViewModel.networkState.observe(viewLifecycleOwner, Observer {
            adapter.setNetworkState(it)
        })
    }
}