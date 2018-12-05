package com.dialogapp.dialog.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dialogapp.dialog.GlideRequests
import com.dialogapp.dialog.R
import com.dialogapp.dialog.databinding.NetworkStateItemBinding
import com.dialogapp.dialog.databinding.PostItemBinding
import com.dialogapp.dialog.model.Post
import com.dialogapp.dialog.repository.NetworkState

class PostsAdapter(
        private val glide: GlideRequests,
        private val retryCallback: () -> Unit)
    : PagedListAdapter<Post, RecyclerView.ViewHolder>(POST_COMPARATOR) {

    private var networkState: NetworkState? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.post_item -> {
                val binding = DataBindingUtil.inflate<PostItemBinding>(LayoutInflater.from(parent.context),
                        R.layout.post_item, parent, false)
                PostViewHolder(binding.root, binding, glide)
            }
            R.layout.network_state_item -> {
                val binding = DataBindingUtil.inflate<NetworkStateItemBinding>(LayoutInflater.from(parent.context),
                        R.layout.network_state_item, parent, false)
                NetworkStateItemViewHolder(binding.root, binding, retryCallback)
            }
            else -> throw IllegalArgumentException("unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(
            holder: RecyclerView.ViewHolder,
            position: Int,
            payloads: MutableList<Any>) {
        if (payloads.isNotEmpty()) {
//            val item = getItem(position)
//            (holder as PostViewHolder).updatePost(item)
        } else {
            onBindViewHolder(holder, position)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.post_item -> (holder as PostViewHolder).bind(getItem(position))
            R.layout.network_state_item -> (holder as NetworkStateItemViewHolder).bind(
                    networkState)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            R.layout.network_state_item
        } else {
            R.layout.post_item
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasExtraRow()) 1 else 0
    }

    fun setNetworkState(newNetworkState: NetworkState?) {
        val previousState = this.networkState
        val hadExtraRow = hasExtraRow()
        this.networkState = newNetworkState
        val hasExtraRow = hasExtraRow()
        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) {
                notifyItemRemoved(super.getItemCount())
            } else {
                notifyItemInserted(super.getItemCount())
            }
        } else if (hasExtraRow && previousState != newNetworkState) {
            notifyItemChanged(itemCount - 1)
        }
    }

    private fun hasExtraRow() = networkState != null && networkState != NetworkState.LOADED

    companion object {
        private const val FAVORITE = "FAVORITE"
        private const val CONVERSATION = "CONVERSATION"
        private const val DATE = "DATE"
        private const val AVATAR = "AVATAR"
        private const val USERNAME = "USERNAME"
        private const val CONTENT = "CONTENT"

        private val POST_COMPARATOR = object : DiffUtil.ItemCallback<Post>() {
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
                    diffBundle.putBoolean(FAVORITE, newItem.microblog.isFavorite!!)

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
    }
}