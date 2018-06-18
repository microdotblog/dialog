package com.dialogapp.dialog.util;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.dialogapp.dialog.BuildConfig;
import com.dialogapp.dialog.R;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SharedPrefUtil {
    private static final String KEY_USER_PREFS = "com.dialogapp.dialog.USER_PREFS";
    private static SharedPreferences defaultPrefs;
    private static SharedPreferences userPrefs;
    private static Boolean releaseNotesSeen;

    @Inject
    public SharedPrefUtil(Application app) {
        defaultPrefs = PreferenceManager.getDefaultSharedPreferences(app);
        userPrefs = app.getSharedPreferences(KEY_USER_PREFS, Context.MODE_PRIVATE);

        migratePreferences();
    }

    public void removeUserPref(String key) {
        if (userPrefs != null && !TextUtils.isEmpty(key)) {
            SharedPreferences.Editor editor = userPrefs.edit();
            editor.remove(key);
            editor.apply();
        }
    }

    public String getStringPreference(String key, String defaultValue, boolean isAppPref) {
        String value;
        if (isAppPref) {
            value = defaultPrefs.getString(key, defaultValue);
        } else {
            value = userPrefs.getString(key, defaultValue);
        }
        return value;
    }

    public void setStringPreference(String key, String value) {
        if (userPrefs != null && !TextUtils.isEmpty(key)) {
            SharedPreferences.Editor editor = userPrefs.edit();
            editor.putString(key, value);
            editor.apply();
        }
    }

    public int getIntegerPreference(String key, int defaultValue, boolean isAppPref) {
        int value;
        if (isAppPref) {
            value = defaultPrefs.getInt(key, defaultValue);
        } else {
            value = userPrefs.getInt(key, defaultValue);
        }
        return value;
    }

    public boolean setIntegerPreference(String key, int value) {
        if (userPrefs != null) {
            SharedPreferences.Editor editor = userPrefs.edit();
            editor.putInt(key, value);
            editor.apply();
            return true;
        }
        return false;
    }

    public boolean getBooleanPreference(String key, boolean defaultValue, boolean isAppPref) {
        boolean value;
        if (isAppPref) {
            value = defaultPrefs.getBoolean(key, defaultValue);
        } else {
            value = userPrefs.getBoolean(key, defaultValue);
        }
        return value;
    }

    public boolean setBooleanPreference(String key, boolean value) {
        if (userPrefs != null) {
            SharedPreferences.Editor editor = userPrefs.edit();
            editor.putBoolean(key, value);
            editor.apply();
            return true;
        }
        return false;
    }

    public boolean isReleaseNotesSeen(Context context) {
        if (releaseNotesSeen == null) {
            PackageInfo info = null;
            try {
                info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                // no op
            }
            // considered seen if first time install or last seen release is up to date
            if (info != null && info.firstInstallTime == info.lastUpdateTime) {
                setReleaseNotesSeen(context);
            } else {
                releaseNotesSeen = getIntegerPreference(context.getString(R.string.pref_latest_release), 0, true)
                        >= BuildConfig.VERSION_CODE;
            }
        }
        return releaseNotesSeen;
    }

    public void setReleaseNotesSeen(Context context) {
        releaseNotesSeen = true;
        defaultPrefs.edit()
                .putInt(context.getString(R.string.pref_latest_release), BuildConfig.VERSION_CODE)
                .apply();
    }

    private void migratePreferences() {
        String[] prefs = new String[] {"token", "username", "avatarurl", "fullname"};

        for (String pref : prefs) {
            if (defaultPrefs.contains(pref)) {
                setStringPreference(pref, defaultPrefs.getString(pref, ""));
                defaultPrefs.edit().remove(pref).apply();
            }
        }
    }
}