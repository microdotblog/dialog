package com.dialogapp.dialog.ui

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.dialogapp.dialog.R
import com.dialogapp.dialog.ui.posting.OnBackPressedListener

class MainActivity : AppCompatActivity() {
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

    private fun setTheme() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val theme = sharedPreferences.getString(getString(R.string.pref_theme),
                getString(R.string.pref_value_theme_white))
        val darkModeControl = sharedPreferences.getBoolean(getString(R.string.pref_dark_mode_control),
                android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.P)
        val darkMode = sharedPreferences.getString(getString(R.string.pref_dark_mode),
                getString(R.string.pref_value_dark_mode_auto))

        when (theme) {
            getString(R.string.pref_value_theme_white) -> setTheme(R.style.AppTheme_White)
            getString(R.string.pref_value_theme_indigo) -> setTheme(R.style.AppTheme_Indigo)
            getString(R.string.pref_value_theme_purple) -> setTheme(R.style.AppTheme_Purple)
            getString(R.string.pref_value_theme_teal) -> setTheme(R.style.AppTheme_Teal)
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
