package com.dialogapp.dialog.util;

import android.arch.lifecycle.LiveData;

public class CacheLiveData<T> extends LiveData<T> {

    public CacheLiveData(T data) {
        postValue(data);
    }

    public static <T> LiveData<T> getAsLiveData(T verifiedAccountData) {
        return new CacheLiveData<>(verifiedAccountData);
    }
}
