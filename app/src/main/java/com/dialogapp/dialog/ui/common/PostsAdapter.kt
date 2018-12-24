package com.dialogapp.dialog.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dialogapp.dialog.GlideRequests
import com.dialogapp.dialog.R
import com.dialogapp.dialog.databinding.EndItemBinding
import com.dialogapp.dialog.databinding.PostItemBinding
import com.dialogapp.dialog.model.Post

class PostsAdapter(private val glide: GlideRequests, private val postClickedListener: PostClickedListener)
    : ListAdapter<Post, RecyclerView.ViewHolder>(POST_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.post_item -> {
                val binding = DataBindingUtil.inflate<PostItemBinding>(LayoutInflater.from(parent.context),
                        R.layout.post_item, parent, false)
                PostViewHolder(binding.root, binding, glide, postClickedListener)
            }
            R.layout.end_item -> {
                val binding = DataBindingUtil.inflate<EndItemBinding>(LayoutInflater.from(parent.context),
                        R.layout.end_item, parent, false)
                EndViewHolder(binding.root)
            }
            else -> throw IllegalArgumentException("unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(
            holder: RecyclerView.ViewHolder,
            position: Int,
            payloads: MutableList<Any>) {
        when (getItemViewType(position)) {
            R.layout.post_item -> {
                if (payloads.isNotEmpty()) {
                    (holder as PostViewHolder).updatePost(payloads)
                } else {
                    onBindViewHolder(holder, position)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as PostViewHolder).bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == itemCount - 1) {
            R.layout.end_item
        } else {
            R.layout.post_item
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        if (holder is PostViewHolder) {
            holder.recycle()
        }
        super.onViewRecycled(holder)
    }
}