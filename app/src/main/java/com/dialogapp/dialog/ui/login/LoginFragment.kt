package com.dialogapp.dialog.ui.login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.dialogapp.dialog.R
import com.dialogapp.dialog.databinding.FragmentLoginBinding
import com.dialogapp.dialog.di.Injector
import com.dialogapp.dialog.ui.util.autoCleared
import com.google.android.material.snackbar.Snackbar

class LoginFragment : Fragment() {

    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var binding by autoCleared<FragmentLoginBinding>()

    private lateinit var viewModel: LoginViewModel

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

        binding.buttonLogin.setOnClickListener {
            viewModel.login(binding.editTextLogin.text.toString())
        }

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(LoginViewModel::class.java)
        viewModel.uiState.observe(viewLifecycleOwner, Observer {
            val uiModel = it ?: return@Observer

            binding.progressBarLogin.visibility = if (uiModel.showProgress) View.VISIBLE else View.INVISIBLE
            binding.buttonLogin.isEnabled = uiModel.enableLoginButton

            if (uiModel.showError != null && !uiModel.showError.consumed) {
                uiModel.showError.consume()?.let {
                    Snackbar.make(binding.constraintLayout, uiModel.showError.peek(), Snackbar.LENGTH_SHORT).show()
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
}