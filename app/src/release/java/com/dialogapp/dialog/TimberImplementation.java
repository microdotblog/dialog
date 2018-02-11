package com.dialogapp.dialog;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import timber.log.Timber;

public class TimberImplementation extends Timber.Tree {
    @Override
    protected void log(int priority, @Nullable String tag, @NonNull String message, @Nullable Throwable t) {

    }
}