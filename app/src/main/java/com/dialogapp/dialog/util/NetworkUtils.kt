package com.dialogapp.dialog.util

import android.content.Context
import android.net.wifi.WifiManager
import com.dialogapp.dialog.vo.Resource
import timber.log.Timber


class NetworkUtil {

    companion object {
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

        fun isConnectedToWifi(context: Context): Boolean {
            val wifiMgr = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

            return if (wifiMgr.isWifiEnabled) {
                val wifiInfo = wifiMgr.connectionInfo
                wifiInfo?.networkId != -1
            } else {
                false
            }
        }
    }
}