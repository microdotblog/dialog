package com.dialogapp.dialog.util;

import android.app.Application;
import android.content.Context;
import android.preference.PreferenceManager;

import com.dialogapp.dialog.R;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PreferencesHelper {

    private final Context context;

    @Inject
    public PreferencesHelper(Application app) {
        this.context = app;
    }

    public String fetchToken(String key) {
        return getSharedPref(key, "");
    }

    public void putToken(String value) {
        writeToSharedPref(context.getString(R.string.pref_token), value);
    }

    public String fetchUsername(String key) {
        return getSharedPref(key, "");
    }

    public void putUsername(String value) {
        writeToSharedPref(context.getString(R.string.pref_username), value);
    }

    public String fetchAvatarUrl(String key) {
        return getSharedPref(key, "");
    }

    public void putAvatarUrl(String value) {
        writeToSharedPref(context.getString(R.string.pref_avatar_url), value);
    }

    private void writeToSharedPref(String key, String value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(key, value)
                .apply();
    }

    private String getSharedPref(String key, String defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(key, defaultValue);
    }
}
