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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = binding.recyclerPosts.layoutManager as LinearLayoutManager
            if (layoutManager.findFirstVisibleItemPosition() > 0) {
                binding.chipNotification.visibility = View.VISIBLE
            } else {
                binding.chipNotification.visibility = View.INVISIBLE
            }
        }
    }

    companion object {
        private val TIMEOUT: Long = TimeUnit.MINUTES.toMillis(15L)

        fun shouldRefresh(lastTimestamp: Long): Boolean {
            return (System.currentTimeMillis() - lastTimestamp > TIMEOUT)
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
        binding.chipNotification.setOnClickListener {
            binding.recyclerPosts.smoothScrollToPosition(0)
        }
        basePagedListViewModel = ViewModelProviders.of(this, viewModelFactory).get(BasePagedListViewModel::class.java)
        basePagedListViewModel.endpointData.observe(viewLifecycleOwner, Observer {
            val endpointData = it ?: return@Observer

            if (shouldRefresh(endpointData.lastFetched)) {
                Timber.d("Refreshing - %s, Timestamp - %s", endpointData.endpoint,
                        endpointData.lastFetched)
                basePagedListViewModel.refresh()
            }
        })

        initAdapter()
        initSwipeToRefresh()
    }

    override fun onResume() {
        super.onResume()
        binding.recyclerPosts.addOnScrollListener(scrollListener)
    }

    override fun onStop() {
        binding.recyclerPosts.removeOnScrollListener(scrollListener)
        super.onStop()
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
        basePagedListViewModel.networkState.observe(viewLifecycleOwner, Observer {
            adapter.setNetworkState(it)
        })
    }
}