package workshop.akbolatss.xchangesrates.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager

/**
 * Author: Akbolat Sadvakassov
 * Date: 22.11.2017
 */

object UtilityMethods {

    /**
     * Check if WiFi is Connected.
     */
    fun isWiFiConnected(context: Context): Boolean {
        val wifiManager =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo

        return wifiInfo.networkId != -1
    }

    /**
     * Check network availability.
     */
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

}
