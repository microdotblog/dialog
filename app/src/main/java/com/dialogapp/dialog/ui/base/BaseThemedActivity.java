package com.dialogapp.dialog.ui.base;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.preference.PreferenceManager;

import com.dialogapp.dialog.R;

public abstract class BaseThemedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String nightMode = sharedPref.getString(getString(R.string.pref_nightMode),
                String.valueOf(AppCompatDelegate.MODE_NIGHT_AUTO));

        getDelegate().setLocalNightMode(Integer.parseInt(nightMode));
        super.onCreate(savedInstanceState);
    }

}
