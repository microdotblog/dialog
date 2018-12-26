package com.dialogapp.dialog.ui.common

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.afollestad.materialdialogs.MaterialDialog
import com.dialogapp.dialog.R
import com.dialogapp.dialog.databinding.FragmentBottomSheetPostBinding
import com.dialogapp.dialog.ui.util.autoCleared
import com.dialogapp.dialog.workers.DeletePostWorker
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetPost : BottomSheetDialogFragment() {

    private var binding by autoCleared<FragmentBottomSheetPostBinding>()

    companion object {
        private const val ID = "id"
        private const val URL = "url"
        private const val DELETABLE = "deletable"

        fun newInstance(postId: String, postUrl: String, deletable: Boolean): BottomSheetPost {
            val bottomSheetPost = BottomSheetPost()
            bottomSheetPost.arguments = bundleOf(ID to postId, URL to postUrl,
                    DELETABLE to deletable)
            return bottomSheetPost
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentBottomSheetPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!arguments?.getBoolean(DELETABLE)!!)
            binding.bottomNavViewPost.menu.removeItem(R.id.post_option_delete)

        binding.bottomNavViewPost.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.post_option_view_link -> {
                    val webpage = Uri.parse(arguments?.getString(URL))
                    val intent = Intent(Intent.ACTION_VIEW, webpage)
                    val packMan = activity?.packageManager
                    if (packMan != null && intent.resolveActivity(packMan) != null) {
                        startActivity(intent)
                    }
                    dismiss()
                }
                R.id.post_option_delete -> {
                    MaterialDialog(this.requireContext())
                            .message(R.string.dialog_delete_post)
                            .show {
                                positiveButton(R.string.yes) { dialog ->
                                    dialog.dismiss()
                                    deletePost()
                                }
                                negativeButton(android.R.string.cancel)
                            }
                    dismiss()
                }
            }
            true
        }
    }

    private fun deletePost() {
        val postId = arguments?.getString(ID)
        if (postId != null) {
            val tag = "DEL_$postId"
            val deletePostRequest = OneTimeWorkRequest.Builder(DeletePostWorker::class.java)
                    .setInputData(DeletePostWorker.createInputData(postId))
                    .addTag(tag)
                    .build()
            WorkManager.getInstance().enqueueUniqueWork(tag, ExistingWorkPolicy.KEEP, deletePostRequest)
        }
    }
}