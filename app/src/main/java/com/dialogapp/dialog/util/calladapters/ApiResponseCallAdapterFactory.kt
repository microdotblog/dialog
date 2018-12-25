package com.dialogapp.dialog.util.calladapters

import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class ApiResponseCallAdapterFactory private constructor() : CallAdapter.Factory() {
    companion object {
        @JvmStatic
        @JvmName("create")
        operator fun invoke() = ApiResponseCallAdapterFactory()
    }

    override fun get(returnType: Type,
                     annotations: Array<Annotation>,
                     retrofit: Retrofit): CallAdapter<*, *>? {
        if (CallAdapter.Factory.getRawType(returnType) != ApiResponseCall::class.java) {
            return null
        }
        if (returnType !is ParameterizedType) {
            throw IllegalStateException(
                    "ApiResponse return type must be parameterized as ApiResponse<Foo> or ApiResponse<out Foo>")
        }
        val bodyType = getParameterUpperBound(0, returnType)
        return ApiResponseCallAdapter<Any>(bodyType)
    }
}