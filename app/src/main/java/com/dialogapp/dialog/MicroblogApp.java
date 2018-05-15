package com.dialogapp.dialog;

import android.app.Activity;
import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

import com.dialogapp.dialog.di.AppInjector;
import com.dialogapp.dialog.util.SharedPrefUtil;
import com.squareup.leakcanary.LeakCanary;

import javax.inject.Inject;

import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import timber.log.Timber;

public class MicroblogApp extends Application implements HasActivityInjector {

    @Inject
    DispatchingAndroidInjector<Activity> dispatchingAndroidInjector;

    @Inject
    SharedPrefUtil sharedPrefUtil;

    @Override
    public void onCreate() {
        super.onCreate();

        AppInjector.init(this);

        String nightMode = sharedPrefUtil.getStringPreference(getString(R.string.pref_nightMode),
                String.valueOf(AppCompatDelegate.MODE_NIGHT_NO));
        AppCompatDelegate.setDefaultNightMode(Integer.parseInt(nightMode));

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);

        Timber.plant(new TimberImplementation());
    }

    @Override
    public DispatchingAndroidInjector<Activity> activityInjector() {
        return dispatchingAndroidInjector;
    }
}
