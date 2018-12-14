package com.dialogapp.dialog.ui.common

import android.os.Bundle
import android.text.TextUtils
import androidx.recyclerview.widget.DiffUtil
import com.dialogapp.dialog.model.Post


const val PK_FAVORITE = "FAVORITE"
const val PK_CONVERSATION = "CONVERSATION"
const val PK_DATE = "DATE"
const val PK_AVATAR = "AVATAR"
const val PK_USERNAME = "USERNAME"
const val PK_CONTENT = "CONTENT"

val POST_COMPARATOR = object : DiffUtil.ItemCallback<Post>() {
    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.microblog.isFavorite == newItem.microblog.isFavorite &&
                oldItem.microblog.isConversation == newItem.microblog.isConversation &&
                TextUtils.equals(oldItem.microblog.dateRelative, newItem.microblog.dateRelative) &&
                TextUtils.equals(oldItem.author.avatar, newItem.author.avatar) &&
                TextUtils.equals(oldItem.author.microblog.username, newItem.author.microblog.username) &&
                TextUtils.equals(oldItem.contentHtml, newItem.contentHtml)
    }

    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id &&
                TextUtils.equals(oldItem.author.microblog.username, newItem.author.microblog.username) &&
                TextUtils.equals(oldItem.datePublished, newItem.datePublished)
    }

    override fun getChangePayload(oldItem: Post, newItem: Post): Any? {
        val diffBundle = Bundle()

        if (oldItem.microblog.isFavorite != newItem.microblog.isFavorite)
            diffBundle.putBoolean(PK_FAVORITE, newItem.microblog.isFavorite)

        if (oldItem.microblog.isConversation != newItem.microblog.isConversation)
            diffBundle.putBoolean(PK_CONVERSATION, newItem.microblog.isConversation!!)

        if (!TextUtils.equals(oldItem.microblog.dateRelative, newItem.microblog.dateRelative))
            diffBundle.putString(PK_DATE, newItem.microblog.dateRelative)

        if (!TextUtils.equals(oldItem.author.avatar, newItem.author.avatar))
            diffBundle.putString(PK_AVATAR, newItem.author.avatar)

        if (!TextUtils.equals(oldItem.author.microblog.username, newItem.author.microblog.username))
            diffBundle.putString(PK_USERNAME, newItem.author.microblog.username)

        if (!TextUtils.equals(oldItem.contentHtml, newItem.contentHtml))
            diffBundle.putString(PK_CONTENT, newItem.contentHtml)

        return if (diffBundle.size() == 0) null else diffBundle
    }
}
