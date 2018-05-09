package com.dialogapp.dialog.api;

import android.support.annotation.NonNull;

import com.orhanobut.hawk.Hawk;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Custom okhttp interceptor to add the auth header to all requests except those containing
 * the dummy header "NO-AUTH".
 * <p>
 * Exposes method to set auth token during runtime.
 */
@Singleton
public class ServiceInterceptor implements Interceptor {
    private String authToken;

    @Inject
    public ServiceInterceptor() {
    }

    public String getAuthToken() {
        if (authToken == null || authToken.isEmpty())
            authToken = Hawk.get("token");
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();

        Request.Builder builder = request.newBuilder();
        if (request.header("NO-AUTH") == null) {
            request = builder.addHeader("Authorization", "Token " + getAuthToken()).build();
        }
        return chain.proceed(request);
    }
}
