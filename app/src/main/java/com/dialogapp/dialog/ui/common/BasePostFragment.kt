package com.dialogapp.dialog.ui.common

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.dialogapp.dialog.R
import com.dialogapp.dialog.model.Post
import timber.log.Timber

abstract class BasePostFragment : Fragment(), PostClickedListener {

    lateinit var navOptions: NavOptions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        navOptions = NavOptions.Builder()
                .setEnterAnim(R.anim.nav_default_enter_anim)
                .setExitAnim(R.anim.nav_default_exit_anim)
                .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
                .setPopExitAnim(R.anim.nav_default_pop_exit_anim)
                .build()
    }

    override fun onAvatarClicked(username: String) {
        val argBundle = bundleOf("username" to username)
        findNavController().navigate(R.id.profile_dest, argBundle, navOptions)
    }

    override fun onFavoriteButtonClicked(postId: String) {

    }

    override fun onConversationButtonClicked(postId: String) {
        val argBundle = bundleOf("convId" to postId)
        findNavController().navigate(R.id.conversation_dest, argBundle, navOptions)
    }

    override fun onMenuItemClicked(menuItemId: Int, item: Post): Boolean {
        return false
    }

    override fun onReplyClicked(id: String, username: String) {

    }

    override fun onLinkClicked(isInternalLink: Boolean, text: String) {
        if (isInternalLink) {
            val argBundle = bundleOf("username" to text)
            findNavController().navigate(R.id.profile_dest, argBundle, navOptions)
        } else {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(text))
            val packMan = activity?.packageManager
            if (packMan != null && intent.resolveActivity(packMan) != null) {
                startActivity(intent)
            }
        }
    }

    override fun onImageClicked(imageUrl: String?) {
        Timber.i(imageUrl)
        val navOptions = NavOptions.Builder()
                .setEnterAnim(R.anim.slide_in_bottom)
                .setPopExitAnim(R.anim.slide_out_bottom)
                .build()
        findNavController().navigate(R.id.image_viewer_dest, bundleOf("imageUrl" to imageUrl),
                navOptions)
    }

    override fun onFollowingItemClicked(username: String) {
        val argBundle = bundleOf("username" to username)
        findNavController().navigate(R.id.profile_dest, argBundle, navOptions)
    }
}