package com.dialogapp.dialog.ui.profile.following

import android.os.Bundle
import android.text.TextUtils
import androidx.recyclerview.widget.DiffUtil
import com.dialogapp.dialog.model.FollowingAccount

const val PK_FOLL_USERNAME = "USERNAME"
const val PK_FOLL_AVATAR = "AVATAR"

val FOLLOWING_COMPARATOR = object : DiffUtil.ItemCallback<FollowingAccount>() {

    override fun areItemsTheSame(oldItem: FollowingAccount, newItem: FollowingAccount): Boolean {
        return TextUtils.equals(oldItem.name, newItem.name)
    }

    override fun areContentsTheSame(oldItem: FollowingAccount, newItem: FollowingAccount): Boolean {
        return TextUtils.equals(oldItem.avatar, newItem.avatar)
                && TextUtils.equals(oldItem.username, newItem.username)
    }

    override fun getChangePayload(oldItem: FollowingAccount, newItem: FollowingAccount): Any? {
        val diffBundle = Bundle()

        if (!TextUtils.equals(oldItem.username, newItem.username))
            diffBundle.putString(PK_FOLL_USERNAME, newItem.username)

        if (!TextUtils.equals(oldItem.avatar, newItem.avatar))
            diffBundle.putString(PK_FOLL_AVATAR, newItem.avatar)

        return if (diffBundle.size() == 0) null else diffBundle
    }
}
