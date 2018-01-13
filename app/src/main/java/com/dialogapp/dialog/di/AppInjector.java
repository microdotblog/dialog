package com.dialogapp.dialog.di;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.dialogapp.dialog.MicroblogApp;

import dagger.android.AndroidInjection;
import dagger.android.HasActivityInjector;

public class AppInjector {
    private AppInjector() {}

    public static void init(MicroblogApp microblogApp) {
        DaggerAppComponent.builder().application(microblogApp)
                .build().inject(microblogApp);
        microblogApp.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {
                handleActivity(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

    private static void handleActivity(Activity activity) {
        if (activity instanceof HasActivityInjector)
            AndroidInjection.inject(activity);
    }
}
