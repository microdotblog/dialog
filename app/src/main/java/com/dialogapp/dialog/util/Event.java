package com.dialogapp.dialog.util;

import androidx.annotation.Nullable;

public class Event<T> {

    private final T content;
    private boolean hasBeenHandled;

    private Event(T content) {
        this.content = content;
    }

    public static <T> Event<T> createEvent(T data) {
        return new Event<>(data);
    }

    public boolean getHasBeenHandled() {
        return hasBeenHandled;
    }

    @Nullable
    public final T getContentIfNotHandled() {
        if (hasBeenHandled)
            return null;
        else {
            hasBeenHandled = true;
            return content;
        }
    }

    public T peekContent() {
        return content;
    }

    private void setHasBeenHandled(boolean hasBeenHandled) {
        this.hasBeenHandled = hasBeenHandled;
    }
}
