package com.dialogapp.dialog;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dialogapp.dialog.ui.MainActivity;
import com.dialogapp.dialog.ui.loginscreen.LoginActivity;
import com.orhanobut.hawk.Hawk;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent;
        if (!Hawk.contains(getString(R.string.pref_token)))
            intent = new Intent(this, LoginActivity.class);
        else {
            intent = new Intent(this, MainActivity.class);
        }

        startActivity(intent);
        finish();
    }
}
