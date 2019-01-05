package com.dialogapp.dialog.ui.base

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dialogapp.dialog.GlideApp
import com.dialogapp.dialog.model.Post
import com.dialogapp.dialog.repository.NetworkState
import com.dialogapp.dialog.ui.common.PagedPostsAdapter
import com.google.android.material.chip.Chip
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

abstract class BasePagedListFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var basePagedListViewModel: BasePagedListViewModel
    lateinit var layoutManager: LinearLayoutManager

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            if (dy != 0) {
                if (dy < 0 && layoutManager.findFirstVisibleItemPosition() > 0) {
                    getNotificationChip().visibility = View.VISIBLE
                } else {
                    getNotificationChip().visibility = View.INVISIBLE
                }
            } else if (layoutManager.findFirstVisibleItemPosition() > 0) {
                Timber.i("Scrolled due to new posts")
                getNotificationChip().visibility = View.VISIBLE
            }
        }
    }

    companion object {
        private val TIMEOUT: Long = TimeUnit.MINUTES.toMillis(15L)

        fun shouldRefresh(lastTimestamp: Long): Boolean {
            return (System.currentTimeMillis() - lastTimestamp > TIMEOUT)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getNotificationChip().setOnClickListener {
            getRecyclerView().smoothScrollToPosition(0)
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
        getRecyclerView().addOnScrollListener(scrollListener)
    }

    override fun onStop() {
        getRecyclerView().removeOnScrollListener(scrollListener)
        super.onStop()
    }

    private fun initSwipeToRefresh() {
        basePagedListViewModel.refreshState.observe(this, Observer {
            getSwipeRefreshLayout().isRefreshing = it == NetworkState.LOADING
        })
        getSwipeRefreshLayout().setOnRefreshListener {
            basePagedListViewModel.refresh()
        }
    }

    private fun initAdapter() {
        val glide = GlideApp.with(this)
        val adapter = PagedPostsAdapter(glide, this) {
            basePagedListViewModel.retry()
        }
        layoutManager = LinearLayoutManager(this.requireContext(), RecyclerView.VERTICAL, false)
        getRecyclerView().layoutManager = layoutManager
        getRecyclerView().adapter = adapter
        basePagedListViewModel.posts.observe(viewLifecycleOwner, Observer<PagedList<Post>> {
            adapter.submitList(it)
        })
        basePagedListViewModel.networkState.observe(viewLifecycleOwner, Observer {
            adapter.setNetworkState(it)
        })
    }

    abstract fun getRecyclerView(): RecyclerView

    abstract fun getSwipeRefreshLayout(): SwipeRefreshLayout

    abstract fun getNotificationChip(): Chip
}