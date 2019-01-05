package com.dialogapp.dialog.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceFragmentCompat
import com.dialogapp.dialog.BuildConfig
import com.dialogapp.dialog.R


class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.app_preferences, rootKey)

        findPreference("about_app").summary = "v".plus(BuildConfig.VERSION_NAME)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?): View {
        val root = super.onCreateView(inflater, container, savedInstanceState)!!

        val navController = activity?.findNavController(R.id.nav_host_main)!!
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        root.findViewById<Toolbar>(R.id.toolbar_settings)
                .setupWithNavController(navController, appBarConfiguration)

        return root
    }
}
