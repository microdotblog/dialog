package com.dialogapp.dialog.ui.conversation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.dialogapp.dialog.R
import com.dialogapp.dialog.databinding.FragmentConversationBinding
import com.dialogapp.dialog.ui.util.autoCleared

class ConversationFragment : Fragment() {

    private var binding by autoCleared<FragmentConversationBinding>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_conversation, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val appBarConfiguration = AppBarConfiguration(setOf(R.id.timeline_dest, R.id.mentions_dest,
                R.id.discover_dest, R.id.profile_dest))
        binding.toolbarConv.setupWithNavController(findNavController(), appBarConfiguration)
        val convId = ConversationFragmentArgs.fromBundle(arguments).convId

        if (savedInstanceState == null) {
            val conversationListFragment = ConversationListFragment.newInstance(convId)
            childFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, conversationListFragment).commit()
        }
    }
}