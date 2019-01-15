package com.dialogapp.dialog.ui.profile.following

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.dialogapp.dialog.GlideRequests
import com.dialogapp.dialog.databinding.FollowingItemBinding
import com.dialogapp.dialog.model.FollowingAccount
import com.dialogapp.dialog.ui.common.PostClickedListener

class FollowingViewHolder(view: View, val binding: FollowingItemBinding,
                          private val glide: GlideRequests,
                          private val postClickedListener: PostClickedListener)
    : RecyclerView.ViewHolder(view) {

    fun bind(followingAccount: FollowingAccount) {
        glide.load(followingAccount.avatar)
                .into(binding.imageThumbnail)
        binding.textFullname.text = followingAccount.name
        binding.textUsername.text = followingAccount.username
        binding.cardFollowing.setOnClickListener {
            postClickedListener.onProfileClicked(followingAccount.username)
        }
    }

    fun updatePost(payloads: MutableList<Any>) {
        val diffBundle = payloads[0] as Bundle

        for (key in diffBundle.keySet()) {
            when (key) {
                PK_FOLL_AVATAR -> {
                    glide.load(diffBundle.getString(PK_FOLL_AVATAR))
                            .into(binding.imageThumbnail)
                }
                PK_FOLL_USERNAME -> {
                    binding.textUsername.text = diffBundle.getString(PK_FOLL_USERNAME)
                }
            }
        }
    }

    fun recycle() {
        glide.clear(binding.imageThumbnail)
    }
}