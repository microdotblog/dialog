package com.dialogapp.dialog;

import android.content.Intent;
import android.os.Bundle;

import com.dialogapp.dialog.ui.MainActivity;
import com.dialogapp.dialog.ui.base.BaseInjectableActivity;
import com.dialogapp.dialog.ui.loginscreen.LoginActivity;
import com.dialogapp.dialog.util.SharedPrefUtil;

import javax.inject.Inject;

public class LauncherActivity extends BaseInjectableActivity {
    @Inject
    SharedPrefUtil sharedPrefUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent;
        if (sharedPrefUtil.getStringPreference(getString(R.string.pref_token), null) == null)
            intent = new Intent(this, LoginActivity.class);
        else {
            intent = new Intent(this, MainActivity.class);
        }

        startActivity(intent);
        finish();
    }
}
