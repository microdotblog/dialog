package com.dialogapp.dialog;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import okhttp3.OkHttpClient;

public class StethoImplementation {
    public static OkHttpClient.Builder addStetho(OkHttpClient.Builder builder) {
        return builder.addNetworkInterceptor(new StethoInterceptor());
    }
}