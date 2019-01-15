package com.dialogapp.dialog.ui.settings

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.dialogapp.dialog.BuildConfig
import com.dialogapp.dialog.R
import com.dialogapp.dialog.ui.MainActivity


class SettingsFragment : PreferenceFragmentCompat(),
        SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.app_preferences, rootKey)

        findPreference(getString(R.string.pref_about_app)).summary = "v".plus(BuildConfig.VERSION_NAME)

        val pref = findPreference(getString(R.string.pref_dark_mode_control)) as SwitchPreferenceCompat
        pref.setSummaryOn(R.string.pref_dark_mode_control_summary_manual)
        pref.setSummaryOff(R.string.pref_dark_mode_control_summary_system)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            pref.isVisible = true
        } else {
            pref.isChecked = true
            pref.summary = getString(R.string.pref_dark_mode_control_summary_manual)
            pref.isVisible = false
        }

        findPreference(getString(R.string.pref_rate_app)).setOnPreferenceClickListener {
            launchMarket()
            true
        }
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

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        activity?.run {
            when (key) {
                getString(R.string.pref_theme), getString(R.string.pref_dark_mode_control) -> {
                    recreate()
                }
                getString(R.string.pref_dark_mode) -> {
                    val value = sharedPreferences.getString(key, getString(R.string.pref_value_dark_mode_disabled))
                    if (value == getString(R.string.pref_value_dark_mode_auto)
                            && Build.VERSION.SDK_INT >= 23 && !isLocationPermissionGranted()) {
                        showMessageAndRequest()
                    } else {
                        recreate()
                    }
                }
                else -> {
                }
            }
        }
    }

    private fun isLocationPermissionGranted(): Boolean {
        activity?.run {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED
        }
    }

    private fun showMessageAndRequest() {
        activity?.run {
            MaterialDialog(this)
                    .message(R.string.location_permission_message)
                    .onDismiss {
                        requestPermissionForLocation()
                    }
                    .show {
                        positiveButton()
                    }
        }
    }

    private fun requestPermissionForLocation() {
        activity?.run {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION),
                    MainActivity.LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    private fun launchMarket() {
        val packageName = requireContext().packageName
        try {
            startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$packageName")))
        } catch (e: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=$packageName")))
        }
    }
}
