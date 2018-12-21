package com.dialogapp.dialog.ui.common

interface PostClickedListener {
    fun onProfileClicked(username: String, postClickedListener: PostClickedListener)

    fun onFavoriteButtonClicked(postId: String)

    fun onConversationButtonClicked(postId: String)

    fun onOverflowMenuClicked(postId: String, postUrl: String, isDeletable: Boolean)

    fun onReplyClicked(id: String, username: String)

    fun onLinkClicked(text: String)

    fun onImageClicked(imageUrl: String?)
}