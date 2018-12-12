package com.dialogapp.dialog.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.dialogapp.dialog.R
import com.dialogapp.dialog.databinding.FragmentMoreBinding
import com.dialogapp.dialog.ui.util.autoCleared

class MoreFragment : Fragment() {

    private var binding by autoCleared<FragmentMoreBinding>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_more, container, false)
        return binding.root
    }

}