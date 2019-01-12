package com.dialogapp.dialog.ui.base

import android.content.Intent
import android.net.Uri
import android.preference.PreferenceManager
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.dialogapp.dialog.R
import com.dialogapp.dialog.ui.common.BottomSheetPost
import com.dialogapp.dialog.ui.common.PostClickedListener
import com.dialogapp.dialog.ui.common.RequestViewModel
import com.dialogapp.dialog.ui.util.ImageGetterOptions
import com.dialogapp.dialog.ui.util.ImageSize
import timber.log.Timber


abstract class BaseFragment : Fragment(), PostClickedListener {

    val profileNavOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.nav_default_enter_anim)
            .setExitAnim(R.anim.nav_default_exit_anim)
            .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
            .setPopExitAnim(R.anim.nav_default_pop_exit_anim)
            .build()

    val convNavOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.nav_default_enter_anim)
            .setExitAnim(R.anim.nav_default_exit_anim)
            .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
            .setPopExitAnim(R.anim.nav_default_pop_exit_anim)
            .build()

    val imageNavOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.nav_default_enter_anim)
            .setExitAnim(R.anim.nav_default_exit_anim)
            .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
            .setPopExitAnim(R.anim.slide_out_bottom)
            .build()

    override fun onProfileClicked(username: String) {
        val argBundle = bundleOf("username" to username)
        findNavController().navigate(R.id.profile_dest, argBundle, profileNavOptions)
    }

    override fun onFavoriteButtonClicked(postId: String?, belongsToEndpoint: String?) {
        val requestViewModel = activity?.run {
            ViewModelProviders.of(this).get(RequestViewModel::class.java)
        }
        requestViewModel?.sendFavoriteRequest(postId, belongsToEndpoint)
    }

    override fun onConversationButtonClicked(postId: String) {
        val argBundle = bundleOf("convId" to postId)
        findNavController().navigate(R.id.conversation_dest, argBundle, convNavOptions)
    }

    override fun onOverflowMenuClicked(postId: String, postUrl: String, username: String,
                                       isDeletable: Boolean) {
        val bottomSheetPost = BottomSheetPost.newInstance(postId, postUrl, username, isDeletable)
        bottomSheetPost.show(childFragmentManager, "bottom_sheet_post")
    }

    override fun onReplyClicked(id: String?, username: String?, content: String?) {
        if (id != null) {
            val navOptions = NavOptions.Builder()
                    .setEnterAnim(R.anim.slide_in_bottom)
                    .setExitAnim(R.anim.nav_default_exit_anim)
                    .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
                    .setPopExitAnim(R.anim.slide_out_bottom)
                    .build()
            val mainNavController = activity?.findNavController(R.id.nav_host_main)
            mainNavController?.navigate(R.id.new_post_dest, bundleOf("isReply" to true,
                    "id" to id, "username" to username, "content" to content), navOptions)
        }
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

    fun getImageGetterOptions(): ImageGetterOptions {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this.requireContext())
        val imageSizePref = prefs.getString(getString(R.string.pref_image_size),
                getString(R.string.pref_value_image_size_medium))
        val loadImagesPref = prefs.getString(getString(R.string.pref_load_images),
                getString(R.string.pref_value_load_images_always))

        val imageSizeOption: ImageSize = when (imageSizePref) {
            getString(R.string.pref_value_image_size_large) -> ImageSize.LARGE
            getString(R.string.pref_value_image_size_medium) -> ImageSize.MEDIUM
            getString(R.string.pref_value_image_size_small) -> ImageSize.SMALL
            else -> {
                ImageSize.MEDIUM
            }
        }
        return ImageGetterOptions(this.requireContext(), imageSizeOption, loadImagesPref!!.toInt())
    }
}