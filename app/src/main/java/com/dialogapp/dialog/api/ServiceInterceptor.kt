package com.dialogapp.dialog.api

import androidx.annotation.NonNull
import com.dialogapp.dialog.testing.OpenForTesting
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Custom okhttp interceptor to add the auth header to all requests except those containing
 * the dummy header "NO-AUTH".
 */
@Singleton
@OpenForTesting
class ServiceInterceptor @Inject constructor() : Interceptor {
    var authToken: String = ""

    @Throws(IOException::class)
    override fun intercept(@NonNull chain: Interceptor.Chain): Response {
        var request = chain.request()

        val builder = request.newBuilder()
        if (request.header("NO-AUTH") == null) {
            request = builder.addHeader("Authorization", "Token $authToken").build()
        }
        return chain.proceed(request)
    }
}
