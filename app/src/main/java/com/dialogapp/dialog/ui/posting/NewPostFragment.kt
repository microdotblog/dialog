package com.dialogapp.dialog.ui.posting

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.SpannableString
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.dialogapp.dialog.R
import com.dialogapp.dialog.databinding.FragmentNewPostBinding
import com.dialogapp.dialog.ui.util.autoCleared
import timber.log.Timber

class NewPostFragment : Fragment(), OnBackPressedListener {

    private var binding by autoCleared<FragmentNewPostBinding>()

    private val textListener = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {
            binding.markwonViewPreview.markdown = p0?.toString()
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentNewPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val postingViewModel = ViewModelProviders.of(this).get(PostingViewModel::class.java)
        binding.bottomBarNewPost.inflateMenu(R.menu.bottom_bar_posting_menu)

        val isReply = NewPostFragmentArgs.fromBundle(arguments!!).isReply
        val username: String
        val content: String
        var postId: String? = null
        if (isReply) {
            postId = NewPostFragmentArgs.fromBundle(arguments!!).id
            username = NewPostFragmentArgs.fromBundle(arguments!!).username
            content = NewPostFragmentArgs.fromBundle(arguments!!).content
            binding.editTextPost.setText("@$username ")
            binding.bottomBarNewPost.menu.removeItem(R.id.posting_title)
            binding.bottomBarNewPost.menu.findItem(R.id.posting_parent_post).setOnMenuItemClickListener {
                MaterialDialog(this.requireContext())
                        .message(text = getHtmlString(content))
                        .show {
                            positiveButton(R.string.dialog_dismiss)
                        }
                true
            }
        } else {
            binding.bottomBarNewPost.menu.removeItem(R.id.posting_parent_post)
            binding.bottomBarNewPost.menu.findItem(R.id.posting_title).setOnMenuItemClickListener {
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

    override fun onResume() {
        super.onResume()
        binding.markwonViewPreview.markdown = binding.editTextPost.text.toString()
        binding.editTextPost.addTextChangedListener(textListener)
        Timber.d("Added Text Watcher")
    }

    override fun onPause() {
        Timber.d("Removed Text Watcher")
        binding.editTextPost.removeTextChangedListener(textListener)
        super.onPause()
    }

    private fun getHtmlString(html: String): SpannableString {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            SpannableString(Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY))
        } else {
            SpannableString(Html.fromHtml(html))
        }
    }
}