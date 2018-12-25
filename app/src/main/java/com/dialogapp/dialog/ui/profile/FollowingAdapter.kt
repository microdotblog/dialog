package com.dialogapp.dialog.ui.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.dialogapp.dialog.GlideRequests
import com.dialogapp.dialog.databinding.FollowingItemBinding
import com.dialogapp.dialog.model.FollowingAccount
import com.dialogapp.dialog.ui.common.PostClickedListener

class FollowingAdapter(private val glide: GlideRequests, private val postClickedListener: PostClickedListener)
    : ListAdapter<FollowingAccount, FollowingViewHolder>(FOLLOWING_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowingViewHolder {
        val binding = FollowingItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FollowingViewHolder(binding.root, binding, glide, postClickedListener)
    }

    override fun onBindViewHolder(
            holder: FollowingViewHolder,
            position: Int,
            payloads: MutableList<Any>) {
        if (payloads.isNotEmpty()) {
            holder.updatePost(payloads)
        } else {
            onBindViewHolder(holder, position)
        }
    }

    override fun onBindViewHolder(holder: FollowingViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onViewRecycled(holder: FollowingViewHolder) {
        holder.recycle()
        super.onViewRecycled(holder)
    }
}
