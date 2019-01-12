package com.dialogapp.dialog.ui.posting

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.SpannableString
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.dialogapp.dialog.R
import com.dialogapp.dialog.databinding.FragmentNewPostBinding
import com.dialogapp.dialog.ui.common.RequestViewModel
import com.dialogapp.dialog.ui.util.autoCleared
import timber.log.Timber

class NewPostFragment : Fragment(), OnBackPressedListener {

    private var binding by autoCleared<FragmentNewPostBinding>()
    private lateinit var requestViewModel: RequestViewModel

    private val textListener = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {
            binding.markwonViewPreview.markdown = p0?.toString()
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestViewModel = activity?.run {
            ViewModelProviders.of(this).get(RequestViewModel::class.java)
        }!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentNewPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bottomBarNewPost.inflateMenu(R.menu.bottom_bar_posting_menu)
        setMenuItemOnClickListeners()

        val isReply = NewPostFragmentArgs.fromBundle(arguments!!).isReply
        val username: String
        val content: String
        var postId: String? = null
        if (isReply) {
            postId = NewPostFragmentArgs.fromBundle(arguments!!).id
            username = NewPostFragmentArgs.fromBundle(arguments!!).username
            content = NewPostFragmentArgs.fromBundle(arguments!!).content
            binding.editTextPost.append("@$username ")
            binding.bottomBarNewPost.menu.removeItem(R.id.posting_title)
            binding.bottomBarNewPost.menu.findItem(R.id.posting_parent_post).setOnMenuItemClickListener {
                MaterialDialog(this.requireContext())
                        .title(text = "@$username wrote")
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
                    input(hint = "optional", prefill = requestViewModel.title) { dialog, text ->
                        requestViewModel.title = text.toString()
                    }
                    positiveButton(R.string.set)
                    negativeButton(R.string.clear) {
                        discardTitle()
                    }
                }
                true
            }
        }

        binding.bottomBarNewPost.setupWithNavController(findNavController())
        binding.bottomBarNewPost.setNavigationOnClickListener {
            showDiscardDialog()
        }

        binding.fab.setOnClickListener {
            MaterialDialog(this.requireContext())
                    .title(R.string.confirm)
                    .show {
                        positiveButton(R.string.send) { dialog ->
                            if (isReply) {
                                requestViewModel.sendReply(postId!!, binding.editTextPost.text.toString())
                            } else {
                                requestViewModel.sendPost(content = binding.editTextPost.text.toString())
                            }
                            discardTitle()
                            dialog.dismiss()
                            findNavController().navigateUp()
                        }
                        negativeButton()
                    }
        }
    }

    override fun onBackPressed() {
        showDiscardDialog()
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

    private fun discardTitle() {
        requestViewModel.title = null
    }

    private fun showDiscardDialog() {
        MaterialDialog(this.requireContext())
                .title(R.string.confirm)
                .message(R.string.discard_post).show {
                    positiveButton(R.string.discard) { dialog ->
                        dialog.dismiss()
                        discardTitle()
                        findNavController().navigateUp()
                    }
                    negativeButton()
                }
    }

    private fun getHtmlString(html: String): SpannableString {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            SpannableString(Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY))
        } else {
            SpannableString(Html.fromHtml(html))
        }
    }


    private fun setMenuItemOnClickListeners() {
        binding.bottomBarNewPost.menu.findItem(R.id.posting_markdown_bold).setOnMenuItemClickListener {
            binding.editTextPost.text?.insert(binding.editTextPost.selectionStart, " **** ")
            binding.editTextPost.setSelection(binding.editTextPost.selectionEnd - 3)
            showKeyboard()
            true
        }

        binding.bottomBarNewPost.menu.findItem(R.id.posting_markdown_italic).setOnMenuItemClickListener {
            binding.editTextPost.text?.insert(binding.editTextPost.selectionStart, " __ ")
            binding.editTextPost.setSelection(binding.editTextPost.selectionEnd - 2)
            showKeyboard()
            true
        }

        binding.bottomBarNewPost.menu.findItem(R.id.posting_markdown_link).setOnMenuItemClickListener {
            MaterialDialog(this.requireContext()).show {
                input(prefill = "https://") { _, text ->
                    binding.editTextPost.text?.insert(binding.editTextPost.selectionStart, " []($text) ")
                    binding.editTextPost.setSelection(binding.editTextPost.selectionEnd - (text.length + 4))
                }
                positiveButton {
                    dismiss()
                    binding.editTextPost.requestFocus()
                    showKeyboard()
                }
                negativeButton()
            }
            true
        }

        binding.bottomBarNewPost.menu.findItem(R.id.posting_markdown_quote).setOnMenuItemClickListener {
            binding.editTextPost.text?.insert(binding.editTextPost.selectionStart, "\n\n> ")
            showKeyboard()
            true
        }
    }

    private fun showKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }
}