package com.dialogapp.dialog.ui.common

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.dialogapp.dialog.GlideRequests
import com.dialogapp.dialog.databinding.PostItemBinding
import com.dialogapp.dialog.model.Post
import com.dialogapp.dialog.ui.util.GlideImageGetter
import com.dialogapp.dialog.ui.util.HtmlTextHelper

class PostViewHolder(view: View, val binding: PostItemBinding, private val glide: GlideRequests)
    : RecyclerView.ViewHolder(view) {

    fun bind(post: Post?) {
        binding.textFullname.text = post?.author?.name
        binding.textUsername.text = post?.author?.microblog?.username
        binding.textTime.text = post?.microblog?.dateRelative
        HtmlTextHelper(glide, post?.contentHtml).setHtmlContent(binding.textContent)
        glide.load(post?.author?.avatar).into(binding.imageThumbnail)
    }

    fun updatePost(payloads: MutableList<Any>) {
        val diffBundle = payloads[0] as Bundle

        for (key in diffBundle.keySet()) {
//            when (key) {
//                CONVERSATION ->
//                FAVORITE ->
//                DATE ->
//                AVATAR ->
//                USERNAME ->
//                CONTENT ->
//            }
        }
    }

    fun recycle() {
        glide.clear(binding.imageThumbnail)
        GlideImageGetter.clear(binding.textContent)
    }
}