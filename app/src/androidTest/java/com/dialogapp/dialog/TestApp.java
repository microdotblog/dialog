package com.dialogapp.dialog;

import android.app.Application;

/**
 * Separate app for tests to prevent initializing dependency injection.
 *
 */
public class TestApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
    }
}
