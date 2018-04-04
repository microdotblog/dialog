package com.dialogapp.dialog;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dialogapp.dialog.ui.loginscreen.LoginActivity;
import com.dialogapp.dialog.ui.mainscreen.MainActivity;
import com.orhanobut.hawk.Hawk;

public class LauncherActivity extends AppCompatActivity {

    public static final String EXTRA_USERNAME = LauncherActivity.class.getName() + ".EXTRA_USERNAME";
    public static final String EXTRA_FULLNAME = LauncherActivity.class.getName() + ".EXTRA_FULLNAME";
    public static final String EXTRA_AVATARURL = LauncherActivity.class.getName() + ".EXTRA_AVATARURL";
    public static final String EXTRA_TOKEN = LauncherActivity.class.getName() + ".EXTRA_TOKEN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent;
        if (!Hawk.contains(getString(R.string.pref_token)))
            intent = new Intent(this, LoginActivity.class);
        else {
            intent = new Intent(this, MainActivity.class);
            intent.putExtra(EXTRA_TOKEN, (String) Hawk.get(getString(R.string.pref_token)));
            intent.putExtra(EXTRA_USERNAME, (String) Hawk.get(getString(R.string.pref_username)));
            intent.putExtra(EXTRA_FULLNAME, (String) Hawk.get(getString(R.string.pref_fullname)));
            intent.putExtra(EXTRA_AVATARURL, (String) Hawk.get(getString(R.string.pref_avatar_url)));
        }

        startActivity(intent);
        finish();
    }
}
