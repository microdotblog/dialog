package com.dialogapp.dialog.util;

import android.arch.lifecycle.LiveData;

/**
 * Returns the argument as LiveData
 *
 * @param <T> Type of the argument
 */
public class CacheLiveData<T> extends LiveData<T> {

    public CacheLiveData(T data) {
        postValue(data);
    }

    public static <T> LiveData<T> getAsLiveData(T verifiedAccountData) {
        return new CacheLiveData<>(verifiedAccountData);
    }
}
