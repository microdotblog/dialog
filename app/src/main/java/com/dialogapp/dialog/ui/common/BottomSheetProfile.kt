package com.dialogapp.dialog.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.dialogapp.dialog.R
import com.dialogapp.dialog.databinding.FragmentSheetProfileOptionsBinding
import com.dialogapp.dialog.ui.profile.ProfileSharedViewModel
import com.dialogapp.dialog.ui.util.autoCleared
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetProfile : BottomSheetDialogFragment() {

    private var binding by autoCleared<FragmentSheetProfileOptionsBinding>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSheetProfileOptionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val profileSharedViewModel = ViewModelProviders.of(parentFragment!!).get(ProfileSharedViewModel::class.java)
        val mainNavController = activity?.findNavController(R.id.nav_host_main)

        binding.bottomNavViewProfile.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.profile_sheet_settings -> {
                    Toast.makeText(this.requireContext(), "Work in progress", Toast.LENGTH_SHORT).show()
                }
                R.id.profile_sheet_logout -> {
                    MaterialDialog(this.requireContext())
                            .title(R.string.confirm)
                            .message(R.string.dialog_logout_message)
                            .positiveButton(R.string.logout) {
                                profileSharedViewModel.logout()
                                mainNavController?.navigate(R.id.action_home_dest_to_login_dest)
                            }
                            .negativeButton()
                            .show()
                    dismiss()
                }
            }
            true
        }
    }
}