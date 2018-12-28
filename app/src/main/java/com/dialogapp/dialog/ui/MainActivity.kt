package com.dialogapp.dialog.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dialogapp.dialog.R
import com.dialogapp.dialog.ui.posting.OnBackPressedListener

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_Light)
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
}
