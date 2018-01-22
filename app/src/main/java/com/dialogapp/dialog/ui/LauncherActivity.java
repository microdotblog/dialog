package com.dialogapp.dialog.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dialogapp.dialog.R;
import com.dialogapp.dialog.ui.loginscreen.LoginActivity;
import com.dialogapp.dialog.ui.mainscreen.MainActivity;
import com.dialogapp.dialog.util.PreferencesHelper;

import javax.inject.Inject;

import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;

public class LauncherActivity extends AppCompatActivity implements HasActivityInjector {

    public static final String EXTRA_USERNAME = "com.dialogapp.dialog.USERNAME";
    public static final String EXTRA_FULLNAME = "com.dialogapp.dialog.FULLNAME";
    public static final String EXTRA_AVATARURL = "com.dialogapp.dialog.AVATAR";

    @Inject
    DispatchingAndroidInjector<Activity> dispatchingAndroidInjector;

    @Inject
    PreferencesHelper preferencesHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String token = preferencesHelper.fetchToken(getString(R.string.pref_token));

        Intent intent;
        if (token.isEmpty())
            intent = new Intent(this, LoginActivity.class);
        else
            intent = new Intent(this, MainActivity.class);

        startActivity(intent);
        finish();
    }

    @Override
    public DispatchingAndroidInjector<Activity> activityInjector() {
        return dispatchingAndroidInjector;
    }
}
