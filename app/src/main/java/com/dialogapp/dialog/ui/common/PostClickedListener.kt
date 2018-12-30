package com.dialogapp.dialog.ui.common

interface PostClickedListener {
    fun onProfileClicked(username: String)

    fun onFavoriteButtonClicked(postId: String?, belongsToEndpoint: String?)

    fun onConversationButtonClicked(postId: String)

    fun onOverflowMenuClicked(postId: String, postUrl: String, isDeletable: Boolean)

    fun onReplyClicked(id: String?, username: String?, content: String?)

    fun onLinkClicked(text: String)

    fun onImageClicked(imageUrl: String?)
}