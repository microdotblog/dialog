package com.dialogapp.dialog.ui.conversation

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import com.dialogapp.dialog.di.Injector
import com.dialogapp.dialog.ui.common.BaseListFragment
import com.dialogapp.dialog.ui.common.EndpointArgs
import com.dialogapp.dialog.vo.CONVERSATION

class ConversationListFragment : BaseListFragment() {

    companion object {
        const val CONVERSATION_ARG = "conversation_arg"
        fun newInstance(convId: String): ConversationListFragment {
            val convListFragment = ConversationListFragment()
            convListFragment.arguments = bundleOf(CONVERSATION_ARG to convId)
            return convListFragment
        }
    }

    override fun onAttach(context: Context?) {
        viewModelFactory = Injector.get().viewModelFactory()
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val convId = arguments?.getString(CONVERSATION_ARG) ?: ""
        baseListViewModel.showEndpoint(EndpointArgs(CONVERSATION, convId))
    }
}