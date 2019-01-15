package com.dialogapp.dialog.util.calladapters

import com.dialogapp.dialog.api.ApiErrorResponse
import com.dialogapp.dialog.api.ApiResponse
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type

class ApiResponseCallAdapter<R>(private val responseType: Type) :
        CallAdapter<R, ApiResponseCall<R>> {

    override fun responseType() = responseType

    override fun adapt(call: Call<R>): ApiResponseCall<R> {
        return ApiResponseCallDelegate(call)
    }
}

interface ApiResponseCall<T> {
    fun cancel()
    fun enqueue(callback: ApiResponseCallback<T>)
}

interface ApiResponseCallback<T> {
    fun onSuccess(response: ApiResponse<T>)
    fun onFailure(response: ApiErrorResponse<T>)
}

private class ApiResponseCallDelegate<T>(private val call: Call<T>)
    : ApiResponseCall<T> {
    override fun enqueue(callback: ApiResponseCallback<T>) {
        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                callback.onSuccess(ApiResponse.create(response))
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                callback.onFailure(ApiResponse.create(t))
            }
        })
    }

    override fun cancel() {
        call.cancel()
    }
}
