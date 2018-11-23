package com.dialogapp.dialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import timber.log.Timber;

public class TimberImplementation extends Timber.DebugTree {
    @Nullable
    @Override
    protected String createStackElementTag(@NonNull StackTraceElement element) {
        return String.format("C:%s:%s",
                super.createStackElementTag(element),
                element.getLineNumber());
    }
}