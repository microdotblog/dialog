package com.dialogapp.dialog.ui

import android.content.pm.PackageManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.dialogapp.dialog.R
import com.dialogapp.dialog.ui.posting.OnBackPressedListener


class MainActivity : AppCompatActivity() {

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    // https://stackoverflow.com/a/52997438
    // TODO : Remove after official support
    override fun onBackPressed() {
        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_main)
        navHost?.let { navFragment ->
            navFragment.childFragmentManager.primaryNavigationFragment?.let { fragment ->
                if (fragment is OnBackPressedListener)
                    fragment.onBackPressed()
                else
                    super.onBackPressed()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE ->
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                    recreate()
                } else {
                    Toast.makeText(this, "Permission Denied. Using default values.",
                            Toast.LENGTH_SHORT).show()
                    recreate()
                }
        }
    }

    private fun setTheme() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val theme = sharedPreferences.getString(getString(R.string.pref_theme),
                getString(R.string.pref_value_theme_white))
        val darkModeControl = sharedPreferences.getBoolean(getString(R.string.pref_dark_mode_control),
                android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.P)
        val darkMode = sharedPreferences.getString(getString(R.string.pref_dark_mode),
                getString(R.string.pref_value_dark_mode_disabled))

        when (theme) {
            getString(R.string.pref_value_theme_white) -> setTheme(R.style.AppTheme_White)
            getString(R.string.pref_value_theme_micro) -> setTheme(R.style.AppTheme_Micro)
            getString(R.string.pref_value_theme_mint) -> setTheme(R.style.AppTheme_Mint)
            getString(R.string.pref_value_theme_classic) -> setTheme(R.style.AppTheme_Classic)
            getString(R.string.pref_value_theme_reply) -> setTheme(R.style.AppTheme_Reply)
        }

        when (darkModeControl) {
            false -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
            true -> {
                when (darkMode) {
                    getString(R.string.pref_value_dark_mode_auto) ->
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO)
                    getString(R.string.pref_value_dark_mode_always) ->
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    getString(R.string.pref_value_dark_mode_disabled) ->
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
        }
    }
}
