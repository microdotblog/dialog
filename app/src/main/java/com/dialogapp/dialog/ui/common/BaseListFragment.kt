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
import com.dialogapp.dialog.GlideApp
import com.dialogapp.dialog.R
import com.dialogapp.dialog.databinding.FragmentListBinding
import com.dialogapp.dialog.ui.util.autoCleared
import com.dialogapp.dialog.vo.Status


abstract class BaseListFragment : BasePostFragment() {

    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var baseListViewModel: BaseListViewModel

    private var binding by autoCleared<FragmentListBinding>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.setLifecycleOwner(viewLifecycleOwner)
        baseListViewModel = ViewModelProviders.of(this, viewModelFactory).get(BaseListViewModel::class.java)

        initAdapter()
        initSwipeToRefresh()
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