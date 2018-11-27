package com.dialogapp.dialog.util

import com.dialogapp.dialog.vo.Resource
import timber.log.Timber

/**
 * Wrap a suspending API [call] in try/catch. In case an exception is thrown, a [Resource.error] is
 * created based on the [errorMessage].
 */
suspend fun <T : Any> safeApiCall(call: suspend () -> Resource<T>, errorMessage: String): Resource<T> {
    return try {
        call()
    } catch (e: Exception) {
        // An exception was thrown when calling the API
        Timber.e(e)
        Resource.error(errorMessage, null)
    }
}