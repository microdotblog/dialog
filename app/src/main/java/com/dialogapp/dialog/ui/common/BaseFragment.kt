package com.dialogapp.dialog.ui.common

import android.content.Intent
import android.net.Uri
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.dialogapp.dialog.R
import com.dialogapp.dialog.workers.FavoriteWorker
import com.dialogapp.dialog.workers.FavoriteWorker.Companion.createInputData
import timber.log.Timber


abstract class BaseFragment : Fragment(), PostClickedListener {

    val profileNavOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.slide_in_left)
            .setExitAnim(R.anim.nav_default_exit_anim)
            .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
            .setPopExitAnim(R.anim.nav_default_pop_exit_anim)
            .build()

    val convNavOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.slide_in_right)
            .setExitAnim(R.anim.nav_default_exit_anim)
            .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
            .setPopExitAnim(R.anim.nav_default_pop_exit_anim)
            .build()

    val imageNavOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.slide_in_bottom)
            .setExitAnim(R.anim.nav_default_exit_anim)
            .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
            .setPopExitAnim(R.anim.slide_out_bottom)
            .build()

    override fun onProfileClicked(username: String) {
        val argBundle = bundleOf("username" to username)
        findNavController().navigate(R.id.profile_dest, argBundle, profileNavOptions)
    }

    override fun onFavoriteButtonClicked(postId: String?, belongsToEndpoint: String?) {
        val tag = "FAV_$postId"
        val favoriteRequest = OneTimeWorkRequest.Builder(FavoriteWorker::class.java)
                .setInputData(createInputData(postId, belongsToEndpoint))
                .addTag(tag)
                .build()
        WorkManager.getInstance().enqueueUniqueWork(tag, ExistingWorkPolicy.KEEP, favoriteRequest)
    }

    override fun onConversationButtonClicked(postId: String) {
        val argBundle = bundleOf("convId" to postId)
        findNavController().navigate(R.id.conversation_dest, argBundle, convNavOptions)
    }

    override fun onOverflowMenuClicked(postId: String, postUrl: String, isDeletable: Boolean) {
        val bottomSheetPost = BottomSheetPost.newInstance(postId, postUrl, isDeletable)
        bottomSheetPost.show(fragmentManager, "bottom_sheet_post")
    }

    override fun onReplyClicked(id: String, username: String) {

    }

    override fun onLinkClicked(text: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(text))
        val packMan = activity?.packageManager
        if (packMan != null && intent.resolveActivity(packMan) != null) {
            startActivity(intent)
        }
    }

    override fun onImageClicked(imageUrl: String?) {
        Timber.i(imageUrl)
        findNavController().navigate(R.id.image_viewer_dest, bundleOf("imageUrl" to imageUrl),
                imageNavOptions)
    }
}