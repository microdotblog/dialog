package com.dialogapp.dialog.ui.base

import android.content.Intent
import android.net.Uri
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.dialogapp.dialog.R
import com.dialogapp.dialog.ui.common.*
import com.dialogapp.dialog.ui.posting.NewPostFragmentDirections
import com.dialogapp.dialog.ui.profile.ProfileFragmentDirections
import com.dialogapp.dialog.ui.util.ImageGetterOptions
import com.dialogapp.dialog.ui.util.ImageSize
import timber.log.Timber


abstract class BaseFragment : Fragment(), PostClickedListener {

    override fun onProfileClicked(username: String) {
        val action = ProfileFragmentDirections.actionGlobalProfileDest(username)
        findNavController().navigate(action)
    }

    override fun onFavoriteButtonClicked(postId: String?, belongsToEndpoint: String?) {
        val requestViewModel = activity?.run {
            ViewModelProviders.of(this).get(RequestViewModel::class.java)
        }
        requestViewModel?.sendFavoriteRequest(postId, belongsToEndpoint)
    }

    override fun onConversationButtonClicked(postId: String) {
        val action = ConversationFragmentDirections.actionGlobalConversationDest(postId)
        findNavController().navigate(action)
    }

    override fun onOverflowMenuClicked(postId: String, postUrl: String, username: String,
                                       isDeletable: Boolean) {
        val bottomSheetPost = BottomSheetPost.newInstance(postId, postUrl, username, isDeletable)
        bottomSheetPost.show(childFragmentManager, "bottom_sheet_post")
    }

    override fun onReplyClicked(id: String?, username: String?, content: String?) {
        if (id != null) {
            val mainNavController = activity?.findNavController(R.id.nav_host_main)
            val action = NewPostFragmentDirections.actionGlobalNewPostDest(id, username, content)
                    .setIsReply(true)
            mainNavController?.navigate(action)
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
        val action = ImageViewerFragmentDirections.actionGlobalImageViewerDest(imageUrl)
        findNavController().navigate(action)
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