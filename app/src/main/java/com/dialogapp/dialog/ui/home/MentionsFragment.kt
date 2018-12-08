package com.dialogapp.dialog.ui.home

import android.content.Context
import android.os.Bundle
import android.view.View
import com.dialogapp.dialog.di.Injector
import com.dialogapp.dialog.ui.common.BasePagedListFragment
import com.dialogapp.dialog.vo.MENTIONS

class MentionsFragment : BasePagedListFragment() {

    override fun onAttach(context: Context?) {
        Injector.get().inject(this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        basePagedListViewModel.showEndpoint(MENTIONS)
    }
}