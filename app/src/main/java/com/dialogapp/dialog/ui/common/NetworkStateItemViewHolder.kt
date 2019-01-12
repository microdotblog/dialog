package com.dialogapp.dialog.ui.common

import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.dialogapp.dialog.R
import com.dialogapp.dialog.databinding.NetworkStateItemBinding
import com.dialogapp.dialog.repository.NetworkState
import com.dialogapp.dialog.repository.PagingStatus

/**
 * A View Holder that can display a loading or have click action.
 * It is used to show the network state of paging.
 */
class NetworkStateItemViewHolder(view: View,
                                 private val binding: NetworkStateItemBinding,
                                 private val retryCallback: () -> Unit)
    : RecyclerView.ViewHolder(view) {

    init {
        binding.retryButton.setOnClickListener {
            retryCallback()
        }
    }

    fun bind(networkState: NetworkState?) {
        val typedValue = TypedValue()
        if (networkState?.status == PagingStatus.FAILED)
            binding.root.context.theme?.resolveAttribute(R.attr.colorError, typedValue, true)
        binding.layoutNetworkState.setBackgroundColor(typedValue.data)
        binding.progressBar.visibility = toVisibility(networkState?.status == PagingStatus.RUNNING)
        binding.retryButton.visibility = toVisibility(networkState?.status == PagingStatus.FAILED)
        binding.errorMsg.visibility = toVisibility(networkState?.msg != null)
        binding.errorMsg.text = binding.root.context.getString(R.string.msg_error_paging)
        binding.errorMsg.setOnClickListener {
            binding.errorMsg.text = networkState?.msg
        }
    }

    companion object {
        fun toVisibility(constraint: Boolean): Int {
            return if (constraint) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }
}
