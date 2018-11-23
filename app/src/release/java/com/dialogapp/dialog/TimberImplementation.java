package com.dialogapp.dialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import timber.log.Timber;

public class TimberImplementation extends Timber.Tree {
    @Override
    protected void log(int priority, @Nullable String tag, @NonNull String message, @Nullable Throwable t) {

    }
}