package com.dialogapp.dialog.ui.common

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dialogapp.dialog.GlideApp
import com.dialogapp.dialog.vo.Status
import com.google.android.material.chip.Chip
import timber.log.Timber


abstract class BaseListFragment : BaseFragment() {

    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var baseListViewModel: BaseListViewModel
    lateinit var layoutManager: LinearLayoutManager

    private lateinit var basePostsAdapter: PostsAdapter

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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getNotificationChip().setOnClickListener {
            getRecyclerView().smoothScrollToPosition(0)
        }

        initViewModel()
        initSwipeToRefresh()
        initRecyclerAdapter()
        observe()
    }

    override fun onResume() {
        super.onResume()
        getRecyclerView().addOnScrollListener(scrollListener)
    }

    override fun onStop() {
        getRecyclerView().removeOnScrollListener(scrollListener)
        super.onStop()
    }

    open fun initViewModel() {
        baseListViewModel = ViewModelProviders.of(this, viewModelFactory).get(BaseListViewModel::class.java)
    }

    open fun onSwipeRefresh() {
        baseListViewModel.refresh()
    }

    open fun initRecyclerAdapter() {
        val glide = GlideApp.with(this)
        basePostsAdapter = PostsAdapter(glide, this)
        layoutManager = LinearLayoutManager(this.requireContext(), RecyclerView.VERTICAL, false)
        getRecyclerView().layoutManager = layoutManager
        getRecyclerView().adapter = basePostsAdapter
    }

    open fun observe() {
        baseListViewModel.endpointResult.observe(viewLifecycleOwner, Observer {
            val result = it ?: return@Observer

            getSwipeRefreshLayout().isRefreshing = result.status == Status.LOADING
            Timber.d("Status=%s Endpoint=%s PostsEmptyOrNull=%s", result.status,
                    result.data?.endpointData?.endpoint, result.data?.postData.isNullOrEmpty())
            if (result.data?.postData != null) {
                basePostsAdapter.submitList(result.data.postData)
            } else if (result.status == Status.ERROR) {
                basePostsAdapter.submitList(emptyList())
            }
        })
    }

    private fun initSwipeToRefresh() {
        getSwipeRefreshLayout().setOnRefreshListener {
            onSwipeRefresh()
        }
    }

    abstract fun getRecyclerView(): RecyclerView

    abstract fun getSwipeRefreshLayout(): SwipeRefreshLayout

    abstract fun getNotificationChip(): Chip
}