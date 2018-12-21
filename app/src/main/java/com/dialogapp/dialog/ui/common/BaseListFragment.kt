package com.dialogapp.dialog.ui.common

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dialogapp.dialog.GlideApp
import com.dialogapp.dialog.R
import com.dialogapp.dialog.databinding.FragmentListBinding
import com.dialogapp.dialog.ui.util.autoCleared
import com.dialogapp.dialog.vo.Status


abstract class BaseListFragment : BaseFragment() {

    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var baseListViewModel: BaseListViewModel

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.setLifecycleOwner(viewLifecycleOwner)
        binding.chipNotification.setOnClickListener {
            binding.recyclerPosts.smoothScrollToPosition(0)
        }
        baseListViewModel = ViewModelProviders.of(this, viewModelFactory).get(BaseListViewModel::class.java)

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
        context?.theme?.resolveAttribute(R.attr.colorSecondary, typedValue, true)
        binding.swipeRefresh.setColorSchemeColors(typedValue.data)
        binding.swipeRefresh.setOnRefreshListener {
            baseListViewModel.refresh()
        }
    }

    private fun initAdapter() {
        val glide = GlideApp.with(this)
        val adapter = PostsAdapter(glide, this)
        binding.recyclerPosts.adapter = adapter
        baseListViewModel.endpointResult.observe(viewLifecycleOwner, Observer {
            val result = it ?: return@Observer

            binding.swipeRefresh.isRefreshing = result.status == Status.LOADING
            adapter.submitList(result.data?.postData)
        })
    }
}