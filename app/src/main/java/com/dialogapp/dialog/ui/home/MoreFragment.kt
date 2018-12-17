package com.dialogapp.dialog.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dialogapp.dialog.GlideApp
import com.dialogapp.dialog.R
import com.dialogapp.dialog.auth.SessionManager
import com.dialogapp.dialog.databinding.FragmentMoreBinding
import com.dialogapp.dialog.di.Injector
import com.dialogapp.dialog.ui.util.autoCleared

class MoreFragment : Fragment() {

    lateinit var sessionManager: SessionManager

    private var binding by autoCleared<FragmentMoreBinding>()

    override fun onAttach(context: Context?) {
        sessionManager = Injector.get().sessionManager()
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_more, container, false)
        GlideApp.with(this)
                .load(sessionManager.user?.gravatarUrl)
                .into(binding.imageAvatar)
        val action = MoreFragmentDirections.actionMoreDestToProfileDest(sessionManager.user?.username
                ?: "").setIsSelf(true)
        binding.buttonProfile.setOnClickListener {
            findNavController().navigate(action)
        }
        return binding.root
    }

}