package com.dialogapp.dialog.ui.posting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.dialogapp.dialog.R
import com.dialogapp.dialog.databinding.FragmentNewPostBinding
import com.dialogapp.dialog.ui.util.autoCleared

class NewPostFragment : Fragment(), OnBackPressedListener {

    private var binding by autoCleared<FragmentNewPostBinding>()
    private var isReply: Boolean = false
    private var postId: String? = null
    private var username: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentNewPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val postingViewModel = ViewModelProviders.of(this).get(PostingViewModel::class.java)

        isReply = NewPostFragmentArgs.fromBundle(arguments!!).isReply
        if (isReply) {
            postId = NewPostFragmentArgs.fromBundle(arguments!!).id
            username = NewPostFragmentArgs.fromBundle(arguments!!).username
            binding.editTextPost.setText("@$username ")
        }

        binding.bottomBarNewPost.setupWithNavController(findNavController())
        binding.bottomBarNewPost.setNavigationOnClickListener {
            MaterialDialog(this.requireContext())
                    .message(R.string.discard_post).show {
                        positiveButton(android.R.string.ok) { dialog ->
                            dialog.dismiss()
                            findNavController().navigateUp()
                        }
                        negativeButton(android.R.string.cancel)
                    }
        }

        binding.fab.setOnClickListener {
            MaterialDialog(this.requireContext())
                    .message(R.string.send_post).show {
                        positiveButton(R.string.yes) { dialog ->
                            if (isReply) {
                                postingViewModel.sendReply(postId!!, binding.editTextPost.text.toString())
                            }
                            dialog.dismiss()
                            findNavController().navigateUp()
                        }
                        negativeButton(android.R.string.cancel)
                    }
        }
    }

    override fun onBackPressed() {
        MaterialDialog(this.requireContext())
                .message(R.string.discard_post).show {
                    positiveButton(android.R.string.ok) { dialog ->
                        dialog.dismiss()
                        findNavController().navigateUp()
                    }
                    negativeButton(android.R.string.cancel)
                }
    }
}