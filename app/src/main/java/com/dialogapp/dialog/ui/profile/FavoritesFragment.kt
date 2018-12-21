package com.dialogapp.dialog.ui.profile

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.dialogapp.dialog.di.Injector
import com.dialogapp.dialog.ui.common.BaseListFragment
import com.dialogapp.dialog.ui.common.EndpointArgs
import com.dialogapp.dialog.ui.common.PostClickedListener
import com.dialogapp.dialog.vo.FAVORITES
import timber.log.Timber

class FavoritesFragment : BaseListFragment() {

    private val profileSharedViewModel: ProfileSharedViewModel by lazy {
        ViewModelProviders.of(parentFragment!!).get(ProfileSharedViewModel::class.java)
    }

    override fun onAttach(context: Context?) {
        viewModelFactory = Injector.get().viewModelFactory()
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        baseListViewModel.showEndpoint(EndpointArgs(FAVORITES))
    }

    override fun onProfileClicked(username: String) {
        if (profileSharedViewModel.isProfileCurrentlyShown(username)) {
            Timber.d("Profile requested is currently on top of stack")
            return
        }
        super.onProfileClicked(username)
    }
}