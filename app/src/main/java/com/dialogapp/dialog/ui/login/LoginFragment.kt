package com.dialogapp.dialog.ui.login

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.dialogapp.dialog.R
import com.dialogapp.dialog.databinding.FragmentLoginBinding
import com.dialogapp.dialog.di.Injector
import com.dialogapp.dialog.ui.util.autoCleared
import com.google.android.material.snackbar.Snackbar

class LoginFragment : Fragment() {

    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var binding by autoCleared<FragmentLoginBinding>()

    private lateinit var viewModel: LoginViewModel

    private val textListener = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {

        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if (binding.textInputLayout.error != null) {
                binding.textInputLayout.error = null
            }
        }

    }

    override fun onAttach(context: Context?) {
        viewModelFactory = Injector.get().viewModelFactory()
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ArrayAdapter.createFromResource(
                context!!,
                R.array.login_type_array,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinner.adapter = adapter
        }

        binding.buttonLogin.setOnClickListener {
            viewModel.login(binding.editTextLogin.text.toString())
        }

        val dialog = MaterialDialog(this.requireContext())
                .title(text = "Sign in using token")
                .message(R.string.login_info_token)
        binding.buttonLoginInfo.setOnClickListener {
            dialog.show()
        }

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(LoginViewModel::class.java)
        viewModel.uiState.observe(viewLifecycleOwner, Observer {
            val uiModel = it ?: return@Observer

            binding.progressBarLogin.visibility = if (uiModel.showProgress) View.VISIBLE else View.GONE
            binding.buttonLogin.isEnabled = uiModel.enableLoginButton

            if (uiModel.showError != null && !uiModel.showError.consumed) {
                uiModel.showError.consume()?.let {
                    if (it.startsWith("I"))
                        binding.textInputLayout.error = it
                    else
                        Snackbar.make(binding.constraintLayout, it, Snackbar.LENGTH_SHORT).show()
                    binding.editTextLogin.requestFocus()
                }
            }

            if (uiModel.showSuccess != null && !uiModel.showSuccess.consumed) {
                uiModel.showSuccess.consume().let {
                    findNavController().navigate(R.id.action_login_dest_to_home_dest)
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        binding.textInputLayout.editText?.addTextChangedListener(textListener)
    }

    override fun onPause() {
        binding.textInputLayout.editText?.removeTextChangedListener(textListener)
        super.onPause()
    }
}