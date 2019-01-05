package com.dialogapp.dialog.ui.common

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dialogapp.dialog.R
import com.dialogapp.dialog.databinding.FragmentListToolbarBinding
import com.dialogapp.dialog.di.Injector
import com.dialogapp.dialog.ui.base.BaseListFragment
import com.dialogapp.dialog.ui.base.EndpointArgs
import com.dialogapp.dialog.ui.util.autoCleared
import com.dialogapp.dialog.vo.CONVERSATION
import com.google.android.material.chip.Chip

class ConversationFragment : BaseListFragment() {

    private var binding by autoCleared<FragmentListToolbarBinding>()

    override fun onAttach(context: Context?) {
        viewModelFactory = Injector.get().viewModelFactory()
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentListToolbarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val appBarConfiguration = AppBarConfiguration(setOf(R.id.timeline_dest, R.id.mentions_dest,
                R.id.discover_dest, R.id.profile_dest))
        binding.toolbarConv.setupWithNavController(findNavController(), appBarConfiguration)

        val convId = ConversationFragmentArgs.fromBundle(arguments!!).convId
        baseListViewModel.showEndpoint(EndpointArgs(CONVERSATION, convId))
    }

    override fun getRecyclerView(): RecyclerView = binding.list.recyclerPosts

    override fun getSwipeRefreshLayout(): SwipeRefreshLayout = binding.list.swipeRefresh

    override fun getNotificationChip(): Chip = binding.list.chipNotification
}