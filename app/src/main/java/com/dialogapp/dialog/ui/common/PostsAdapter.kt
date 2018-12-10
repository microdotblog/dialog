package com.dialogapp.dialog.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import com.dialogapp.dialog.GlideRequests
import com.dialogapp.dialog.R
import com.dialogapp.dialog.databinding.PostItemBinding
import com.dialogapp.dialog.model.Post

class PostsAdapter(private val glide: GlideRequests)
    : ListAdapter<Post, PostViewHolder>(POST_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = DataBindingUtil.inflate<PostItemBinding>(LayoutInflater.from(parent.context),
                R.layout.post_item, parent, false)
        return PostViewHolder(binding.root, binding, glide)
    }

    override fun onBindViewHolder(
            holder: PostViewHolder,
            position: Int,
            payloads: MutableList<Any>) {
        if (payloads.isNotEmpty()) {
            holder.updatePost(payloads)
        } else {
            onBindViewHolder(holder, position)
        }
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onViewRecycled(holder: PostViewHolder) {
        holder.recycle()
        super.onViewRecycled(holder)
    }
}