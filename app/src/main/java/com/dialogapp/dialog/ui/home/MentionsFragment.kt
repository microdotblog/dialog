package com.dialogapp.dialog.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dialogapp.dialog.databinding.FragmentListBinding
import com.dialogapp.dialog.di.Injector
import com.dialogapp.dialog.ui.base.BasePagedListFragment
import com.dialogapp.dialog.ui.util.autoCleared
import com.dialogapp.dialog.vo.MENTIONS
import com.google.android.material.chip.Chip

class MentionsFragment : BasePagedListFragment() {

    private var binding by autoCleared<FragmentListBinding>()

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
        basePagedListViewModel.showEndpoint(MENTIONS)
    }

    override fun getRecyclerView(): RecyclerView = binding.recyclerPosts

    override fun getSwipeRefreshLayout(): SwipeRefreshLayout = binding.swipeRefresh

    override fun getNotificationChip(): Chip = binding.chipNotification
}