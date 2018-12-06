package com.dialogapp.dialog.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import com.dialogapp.dialog.R
import com.dialogapp.dialog.databinding.FragmentHomeBinding
import com.dialogapp.dialog.ui.util.autoCleared

class HomeFragment : Fragment() {

    private var binding by autoCleared<FragmentHomeBinding>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bottomNavigationHome.setupWithNavController(Navigation.findNavController(view.findViewById(R.id.nav_host_home)))
    }
}