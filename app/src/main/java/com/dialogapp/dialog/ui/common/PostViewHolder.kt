package com.dialogapp.dialog.ui.common

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.dialogapp.dialog.GlideRequests
import com.dialogapp.dialog.R
import com.dialogapp.dialog.databinding.PostItemBinding
import com.dialogapp.dialog.model.Post
import com.dialogapp.dialog.ui.util.GlideImageGetter
import com.dialogapp.dialog.ui.util.HtmlTextHelper
import com.dialogapp.dialog.ui.util.ImageGetterOptions

class PostViewHolder(private val view: View, val binding: PostItemBinding, private val glide: GlideRequests,
                     private val postClickedListener: PostClickedListener, private val imageGetterOptions: ImageGetterOptions)
    : RecyclerView.ViewHolder(view) {

    fun bind(post: Post?) {
        binding.textFullname.text = post?.author?.name
        binding.textUsername.text = String.format(view.resources.getString(R.string.post_username),
                post?.author?.microblog?.username)
        binding.textTime.text = String.format(view.resources.getString(R.string.post_time),
                post?.microblog?.dateRelative)
        HtmlTextHelper(glide, postClickedListener, imageGetterOptions, post?.contentHtml)
                .setHtmlContent(binding.textContent)
        glide.load(post?.author?.avatar).into(binding.imageThumbnail)

        binding.buttonReply.setOnClickListener {
            postClickedListener.onReplyClicked(post?.id, post?.author?.microblog?.username, post?.contentHtml)
        }

        binding.buttonConv.visibility = when (post?.microblog?.isConversation) {
            true -> {
                binding.buttonConv.setOnClickListener {
                    postClickedListener.onConversationButtonClicked(post.id)
                }
                View.VISIBLE
            }
            else -> {
                View.INVISIBLE
            }
        }

        when (post?.microblog?.isFavorite) {
            true -> binding.buttonFav.setImageResource(R.drawable.ic_round_favorite_24px)
            else -> binding.buttonFav.setImageResource(R.drawable.ic_round_favorite_border_24px)
        }
        binding.buttonFav.setOnClickListener {
            postClickedListener.onFavoriteButtonClicked(post?.id, post?.belongsToEndpoint)
        }

        binding.imageThumbnail.setOnClickListener {
            postClickedListener.onProfileClicked(post?.author?.microblog?.username!!)
        }

        binding.buttonOverflow.setOnClickListener {
            if (post != null) {
                postClickedListener.onOverflowMenuClicked(post.id, post.url,
                        post.author.microblog?.username!!, post.microblog.isDeletable)
            }
        }
    }

    fun updatePost(payloads: MutableList<Any>, post: Post?) {
        val diffBundle = payloads[0] as Bundle

        for (key in diffBundle.keySet()) {
            when (key) {
                PK_CONVERSATION -> {
                    when (diffBundle.getBoolean(PK_CONVERSATION)) {
                        true -> {
                            binding.buttonConv.visibility = View.VISIBLE
                            binding.buttonConv.setOnClickListener {
                                if (post?.id != null)
                                    postClickedListener.onConversationButtonClicked(postId = post.id)
                            }
                        }
                        false -> {
                            binding.buttonConv.visibility = View.INVISIBLE
                        }
                    }
                }
                PK_FAVORITE -> {
                    when (diffBundle.getBoolean(PK_FAVORITE)) {
                        true -> binding.buttonFav.setImageResource(R.drawable.ic_round_favorite_24px)
                        else -> binding.buttonFav.setImageResource(R.drawable.ic_round_favorite_border_24px)
                    }
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
                    HtmlTextHelper(glide, postClickedListener, imageGetterOptions, diffBundle.getString(PK_CONTENT))
                            .setHtmlContent(binding.textContent)
                }
            }
        }
    }

    fun recycle() {
        binding.textFullname.text = null
        binding.textUsername.text = null
        binding.textTime.text = null
        binding.textContent.text = null

        glide.clear(binding.imageThumbnail)
        GlideImageGetter.clear(binding.textContent)
    }
}