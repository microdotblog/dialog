package com.dialogapp.dialog.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dialogapp.dialog.GlideRequests
import com.dialogapp.dialog.R
import com.dialogapp.dialog.databinding.NetworkStateItemBinding
import com.dialogapp.dialog.databinding.PostItemBinding
import com.dialogapp.dialog.model.Post
import com.dialogapp.dialog.repository.NetworkState

class PagedPostsAdapter(
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
            (holder as PostViewHolder).updatePost(payloads)
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

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        if(holder is PostViewHolder) {
            holder.recycle()
        }
        super.onViewRecycled(holder)
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
}