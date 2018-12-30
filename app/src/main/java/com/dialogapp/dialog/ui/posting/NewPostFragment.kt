package com.dialogapp.dialog.ui.posting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
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
        binding.bottomBarNewPost.inflateMenu(R.menu.bottom_bar_posting_menu)

        isReply = NewPostFragmentArgs.fromBundle(arguments!!).isReply
        if (isReply) {
            postId = NewPostFragmentArgs.fromBundle(arguments!!).id
            username = NewPostFragmentArgs.fromBundle(arguments!!).username
            binding.editTextPost.setText("@$username ")
            binding.bottomBarNewPost.menu.removeItem(R.id.posting_title)
        } else {
            binding.bottomBarNewPost.menu[0].setOnMenuItemClickListener {
                MaterialDialog(this.requireContext()).title(text = "Post Title").show {
                    input(hint = "optional", prefill = postingViewModel.title) { dialog, text ->
                        postingViewModel.title = text.toString()
                    }
                    positiveButton(R.string.set)
                    negativeButton(R.string.clear) {
                        postingViewModel.title = null
                    }
                }
                true
            }
        }

        binding.bottomBarNewPost.setupWithNavController(findNavController())
        binding.bottomBarNewPost.setNavigationOnClickListener {
            MaterialDialog(this.requireContext())
                    .title(R.string.confirm)
                    .message(R.string.discard_post).show {
                        positiveButton(R.string.discard) { dialog ->
                            dialog.dismiss()
                            findNavController().navigateUp()
                        }
                        negativeButton(android.R.string.cancel)
                    }
        }

        binding.fab.setOnClickListener {
            MaterialDialog(this.requireContext())
                    .title(R.string.confirm)
                    .show {
                        positiveButton(R.string.send) { dialog ->
                            if (isReply) {
                                postingViewModel.sendReply(postId!!, binding.editTextPost.text.toString())
                            } else {
                                postingViewModel.sendPost(binding.editTextPost.text.toString())
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
                .title(R.string.confirm)
                .message(R.string.discard_post).show {
                    positiveButton(R.string.discard) { dialog ->
                        dialog.dismiss()
                        findNavController().navigateUp()
                    }
                    negativeButton(android.R.string.cancel)
                }
    }
}