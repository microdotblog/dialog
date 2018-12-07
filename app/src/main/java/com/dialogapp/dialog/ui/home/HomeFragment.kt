package com.dialogapp.dialog.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.dialogapp.dialog.R
import com.dialogapp.dialog.auth.SessionManager
import com.dialogapp.dialog.databinding.FragmentHomeBinding
import com.dialogapp.dialog.di.Injector
import com.dialogapp.dialog.ui.util.autoCleared
import javax.inject.Inject

class HomeFragment : Fragment() {

    @Inject
    lateinit var sessionManager: SessionManager

    private var binding by autoCleared<FragmentHomeBinding>()

    override fun onAttach(context: Context?) {
        Injector.get().inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!sessionManager.isLoggedIn) {
            findNavController().popBackStack()
            findNavController().navigate(R.id.login_dest)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = Navigation.findNavController(view.findViewById(R.id.nav_host_home))
        binding.bottomNavigationHome.setupWithNavController(navController)
    }
}