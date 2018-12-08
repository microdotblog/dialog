package com.dialogapp.dialog.ui.common

import android.os.Bundle
import androidx.recyclerview.widget.DiffUtil
import com.dialogapp.dialog.model.Post


const val FAVORITE = "FAVORITE"
const val CONVERSATION = "CONVERSATION"
const val DATE = "DATE"
const val AVATAR = "AVATAR"
const val USERNAME = "USERNAME"
const val CONTENT = "CONTENT"

val POST_COMPARATOR = object : DiffUtil.ItemCallback<Post>() {
    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.microblog.isFavorite == newItem.microblog.isFavorite &&
                oldItem.microblog.isConversation == newItem.microblog.isConversation &&
                oldItem.microblog.dateRelative == newItem.microblog.dateRelative &&
                oldItem.author.avatar == newItem.author.avatar &&
                oldItem.author.microblog.username == newItem.author.microblog.username &&
                oldItem.contentHtml == newItem.contentHtml
    }

    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id &&
                oldItem.author.microblog.username == newItem.author.microblog.username &&
                oldItem.datePublished == newItem.datePublished
    }

    override fun getChangePayload(oldItem: Post, newItem: Post): Any? {
        val diffBundle = Bundle()

        if (oldItem.microblog.isFavorite != newItem.microblog.isFavorite)
            diffBundle.putBoolean(FAVORITE, newItem.microblog.isFavorite)

        if (oldItem.microblog.isConversation != newItem.microblog.isConversation)
            diffBundle.putBoolean(CONVERSATION, newItem.microblog.isConversation!!)

        if (oldItem.microblog.dateRelative != newItem.microblog.dateRelative)
            diffBundle.putString(DATE, newItem.microblog.dateRelative)

        if (oldItem.author.avatar != newItem.author.avatar)
            diffBundle.putString(AVATAR, newItem.author.avatar)

        if (oldItem.author.microblog.username != newItem.author.microblog.username)
            diffBundle.putString(USERNAME, newItem.author.microblog.username)

        if (oldItem.contentHtml != newItem.contentHtml)
            diffBundle.putString(CONTENT, newItem.contentHtml)

        return if (diffBundle.size() == 0) null else diffBundle
    }
}
