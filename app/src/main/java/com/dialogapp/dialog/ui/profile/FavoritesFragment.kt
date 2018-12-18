package com.dialogapp.dialog.ui.profile

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import com.dialogapp.dialog.di.Injector
import com.dialogapp.dialog.ui.common.BaseListFragment
import com.dialogapp.dialog.ui.common.EndpointArgs
import com.dialogapp.dialog.vo.FAVORITES

class FavoritesFragment : BaseListFragment() {

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
        baseListViewModel.showEndpoint(EndpointArgs(FAVORITES))
    }
}