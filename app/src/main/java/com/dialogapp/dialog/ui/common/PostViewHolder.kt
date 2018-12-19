package com.dialogapp.dialog.ui.common

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.dialogapp.dialog.GlideRequests
import com.dialogapp.dialog.databinding.PostItemBinding
import com.dialogapp.dialog.model.Post
import com.dialogapp.dialog.ui.util.GlideImageGetter
import com.dialogapp.dialog.ui.util.HtmlTextHelper

class PostViewHolder(view: View, val binding: PostItemBinding, private val glide: GlideRequests,
                     private val postClickedListener: PostClickedListener)
    : RecyclerView.ViewHolder(view) {

    fun bind(post: Post?) {
        binding.textFullname.text = post?.author?.name
        binding.textUsername.text = post?.author?.microblog?.username
        binding.textTime.text = post?.microblog?.dateRelative
        HtmlTextHelper(glide, postClickedListener, post?.contentHtml).setHtmlContent(binding.textContent)
        glide.load(post?.author?.avatar).into(binding.imageThumbnail)

        binding.buttonConv.visibility = if (post?.microblog?.isConversation!!) {
            binding.buttonConv.setOnClickListener {
                postClickedListener.onConversationButtonClicked(post?.id)
            }
            View.VISIBLE
        } else {
            View.INVISIBLE
        }

        binding.imageThumbnail.setOnClickListener {
            postClickedListener.onProfileClicked(post?.author?.microblog?.username,
                    postClickedListener)
        }
    }

    fun updatePost(payloads: MutableList<Any>) {
        val diffBundle = payloads[0] as Bundle

        for (key in diffBundle.keySet()) {
            when (key) {
                PK_CONVERSATION -> {
                    binding.buttonConv.visibility = if (diffBundle.getBoolean(PK_CONVERSATION))
                        View.VISIBLE
                    else
                        View.GONE
                }
                PK_FAVORITE -> {

                }
                PK_DATE -> {
                    binding.textTime.text = diffBundle.getString(PK_DATE)
                }
                PK_AVATAR -> {
                    glide.load(diffBundle.getString(PK_AVATAR))
                            .into(binding.imageThumbnail)
                }
                PK_USERNAME -> {
                    binding.textUsername.text = diffBundle.getString(PK_USERNAME)
                }
                PK_CONTENT -> {
                    HtmlTextHelper(glide, postClickedListener, diffBundle.getString(PK_CONTENT))
                            .setHtmlContent(binding.textContent)
                }
            }
        }
    }

    fun recycle() {
        glide.clear(binding.imageThumbnail)
        GlideImageGetter.clear(binding.textContent)
    }
}