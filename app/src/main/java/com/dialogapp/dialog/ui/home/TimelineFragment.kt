package com.dialogapp.dialog.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dialogapp.dialog.R
import com.dialogapp.dialog.databinding.FragmentListFabBinding
import com.dialogapp.dialog.di.Injector
import com.dialogapp.dialog.ui.base.BasePagedListFragment
import com.dialogapp.dialog.ui.posting.NewPostFragmentDirections
import com.dialogapp.dialog.ui.util.autoCleared
import com.dialogapp.dialog.vo.TIMELINE
import com.google.android.material.chip.Chip

class TimelineFragment : BasePagedListFragment() {

    private var binding by autoCleared<FragmentListFabBinding>()

    override fun onAttach(context: Context?) {
        viewModelFactory = Injector.get().viewModelFactory()
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentListFabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fabNewPost.setOnClickListener {
            activity?.run {
                val action = NewPostFragmentDirections.actionGlobalNewPostDest(null,
                        null, null)
                findNavController(R.id.nav_host_main).navigate(action)
            }
        }
        basePagedListViewModel.showEndpoint(TIMELINE)
    }

    override fun getRecyclerView(): RecyclerView = binding.list.recyclerPosts

    override fun getSwipeRefreshLayout(): SwipeRefreshLayout = binding.list.swipeRefresh

    override fun getNotificationChip(): Chip = binding.list.chipNotification
}