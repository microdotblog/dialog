package com.dialogapp.dialog.ui.common

import com.dialogapp.dialog.model.Post

interface PostClickedListener {
    fun onAvatarClicked(username: String)

    fun onFavoriteButtonClicked(postId: String)

    fun onConversationButtonClicked(postId: String)

    fun onMenuItemClicked(menuItemId: Int, item: Post): Boolean

    fun onReplyClicked(id: String, username: String)

    fun onLinkClicked(isInternalLink: Boolean, text: String)

    fun onImageClicked(imageUrl: String?)
}