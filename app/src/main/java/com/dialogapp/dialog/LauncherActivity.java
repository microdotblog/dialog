package com.dialogapp.dialog;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dialogapp.dialog.ui.MainActivity;
import com.dialogapp.dialog.ui.loginscreen.LoginActivity;
import com.orhanobut.hawk.Hawk;

import static com.dialogapp.dialog.ui.MainActivity.EXTRA_AVATARURL;
import static com.dialogapp.dialog.ui.MainActivity.EXTRA_FULLNAME;
import static com.dialogapp.dialog.ui.MainActivity.EXTRA_TOKEN;
import static com.dialogapp.dialog.ui.MainActivity.EXTRA_USERNAME;

public class LauncherActivity extends AppCompatActivity {

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
