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
    private static SharedPreferences preferences;
    private static Boolean releaseNotesSeen;

    @Inject
    public SharedPrefUtil(Application app) {
        preferences = PreferenceManager.getDefaultSharedPreferences(app);
    }

    public void remove(String key) {
        if (preferences != null && !TextUtils.isEmpty(key)) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove(key);
            editor.apply();
        }
    }

    public String getStringPreference(String key, String defaultValue) {
        String value = null;

        if (preferences != null) {
            value = preferences.getString(key, defaultValue);
        }
        return value;
    }

    public void setStringPreference(String key, String value) {
        if (preferences != null && !TextUtils.isEmpty(key)) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(key, value);
            editor.apply();
        }
    }

    public float getFloatPreference(String key, float defaultValue) {
        float value = defaultValue;
        if (preferences != null) {
            value = preferences.getFloat(key, defaultValue);
        }
        return value;
    }

    public boolean setFloatPreference(String key, float value) {
        if (preferences != null) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putFloat(key, value);
            editor.apply();
            return true;
        }
        return false;
    }

    public long getLongPreference(String key, long defaultValue) {
        long value = defaultValue;
        if (preferences != null) {
            value = preferences.getLong(key, defaultValue);
        }
        return value;
    }

    public boolean setLongPreference(String key, long value) {
        if (preferences != null) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putLong(key, value);
            editor.apply();
            return true;
        }
        return false;
    }

    public int getIntegerPreference(String key, int defaultValue) {
        int value = defaultValue;
        if (preferences != null) {
            value = preferences.getInt(key, defaultValue);
        }
        return value;
    }

    public boolean setIntegerPreference(String key, int value) {
        if (preferences != null) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(key, value);
            editor.apply();
            return true;
        }
        return false;
    }

    public boolean getBooleanPreference(String key, boolean defaultValue) {
        boolean value = defaultValue;
        if (preferences != null) {
            value = preferences.getBoolean(key, defaultValue);
        }
        return value;
    }

    public boolean setBooleanPreference(String key, boolean value) {
        if (preferences != null) {
            SharedPreferences.Editor editor = preferences.edit();
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
                releaseNotesSeen = getIntegerPreference(context.getString(R.string.pref_latest_release), 0)
                        >= BuildConfig.VERSION_CODE;
            }
        }
        return releaseNotesSeen;
    }

    public void setReleaseNotesSeen(Context context) {
        releaseNotesSeen = true;
        setIntegerPreference(context.getString(R.string.pref_latest_release), BuildConfig.VERSION_CODE);
    }
}