package com.dialogapp.dialog.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dialogapp.dialog.GlideApp
import com.dialogapp.dialog.databinding.FragmentListBinding
import com.dialogapp.dialog.ui.util.autoCleared
import com.dialogapp.dialog.vo.Status
import timber.log.Timber


abstract class BaseListFragment : BaseFragment() {

    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var baseListViewModel: BaseListViewModel

    var binding by autoCleared<FragmentListBinding>()

    private lateinit var basePostsAdapter: PostsAdapter

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.chipNotification.setOnClickListener {
            binding.recyclerPosts.smoothScrollToPosition(0)
        }

        initViewModel()
        initSwipeToRefresh()
        initRecyclerAdapter()
        observe()
    }

    override fun onResume() {
        super.onResume()
        binding.recyclerPosts.addOnScrollListener(scrollListener)
    }

    override fun onStop() {
        binding.recyclerPosts.removeOnScrollListener(scrollListener)
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
        binding.recyclerPosts.adapter = basePostsAdapter
    }

    open fun observe() {
        baseListViewModel.endpointResult.observe(viewLifecycleOwner, Observer {
            val result = it ?: return@Observer

            binding.swipeRefresh.isRefreshing = result.status == Status.LOADING
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
        binding.swipeRefresh.setOnRefreshListener {
            onSwipeRefresh()
        }
    }
}