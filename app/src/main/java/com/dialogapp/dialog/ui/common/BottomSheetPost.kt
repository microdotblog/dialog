package com.dialogapp.dialog.ui.common

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.afollestad.materialdialogs.MaterialDialog
import com.dialogapp.dialog.R
import com.dialogapp.dialog.databinding.FragmentBottomSheetPostBinding
import com.dialogapp.dialog.ui.util.autoCleared
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

        binding.buttonBrowser.setOnClickListener {
            val webpage = Uri.parse(arguments?.getString(URL))
            val intent = Intent(Intent.ACTION_VIEW, webpage)
            val packMan = activity?.packageManager
            if (packMan != null && intent.resolveActivity(packMan) != null) {
                startActivity(intent)
            }
            this.dismiss()
        }

        if (arguments?.getBoolean(DELETABLE)!!) {
            binding.buttonDelete.visibility = View.VISIBLE

            binding.buttonDelete.setOnClickListener {
                MaterialDialog(this.requireContext())
                        .message(R.string.dialog_delete_post)
                        .show {
                            positiveButton(android.R.string.yes) { dialog ->
                                dialog.dismiss()
                            }
                            negativeButton(android.R.string.no)
                        }
                this.dismiss()
            }
        }
    }
}