package com.dialogapp.dialog.ui.home

import android.content.Context
import android.os.Bundle
import android.view.View
import com.dialogapp.dialog.di.Injector
import com.dialogapp.dialog.ui.common.BasePagedListFragment
import com.dialogapp.dialog.vo.TIMELINE

class TimelineFragment: BasePagedListFragment() {

    override fun onAttach(context: Context?) {
        viewModelFactory = Injector.get().viewModelFactory()
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        basePagedListViewModel.showEndpoint(TIMELINE)
    }
}