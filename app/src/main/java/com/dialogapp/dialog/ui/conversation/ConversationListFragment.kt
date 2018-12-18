package com.dialogapp.dialog.ui.conversation

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
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

    // https://stackoverflow.com/a/22673871
    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        return if (!enter && parentFragment != null) {
            val dummyAnimation = AlphaAnimation(1f, 1f)
            dummyAnimation.duration = android.R.integer.config_mediumAnimTime.toLong()
            dummyAnimation
        } else super.onCreateAnimation(transit, enter, nextAnim)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val convId = arguments?.getString(CONVERSATION_ARG) ?: ""
        baseListViewModel.showEndpoint(EndpointArgs(CONVERSATION, convId))
    }
}