package workshop.akbolatss.xchangesrates.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Build
import android.support.v4.app.NotificationManagerCompat
import android.text.format.DateUtils
import android.util.Log
import workshop.akbolatss.xchangesrates.model.response.ChartData
import workshop.akbolatss.xchangesrates.utils.Constants.NOTIFICATION_CHANNEL_ID
import workshop.akbolatss.xchangesrates.utils.Constants.NOTIFICATION_CHANNEL_NAME
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Author: Akbolat Sadvakassov
 * Date: 22.11.2017
 */

object UtilityMethods {

    val todayDate: String
        get() {
            val date = Date()
            return SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH).format(date)
        }

    fun getTimestamp(bpiDate: String): Long {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        var date: Date? = null
        try {
            date = format.parse(bpiDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return date!!.time
    }

    fun getCalculatedBackDays(daysAgo: Int): String {
        val date = Date()
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.DAY_OF_YEAR, daysAgo)
        return SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(calendar.time)
    }

    fun convertTime(timestamp: Long): String {
        try {
            val niceDateStr = DateUtils.getRelativeTimeSpanString(timestamp, Calendar.getInstance().timeInMillis, DateUtils.MINUTE_IN_MILLIS)
            return niceDateStr.toString()
        } catch (e: ParseException) {
            Log.e("ParseException", "Unparseable date " + e.message)
            return timestamp.toString()
        }
    }

    /**
     * Check if WiFi is Connected.
     */
    fun isWiFiConnected(context: Context): Boolean {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo

        return wifiInfo.networkId != -1
    }

    /**
     * Check network availability.
     */
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    /**
     * Generate Channel Id
     */
    fun generateChannelId(chartData: ChartData): String {
        return "${chartData.coin}/${chartData.currency} ${chartData.id}"
    }

    /**
     * Create default notification channel
     */
    fun createDefaultNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_MIN
            )

            val manager = context.getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(serviceChannel)
        }
    }

    /**
     * Create specific notificaition channel
     * @see generateChannelId(chartData)
     */
    fun createNotificationChannel(chartData: ChartData, context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                    generateChannelId(chartData),
                    "${chartData.coin.toUpperCase()}/${chartData.currency.toUpperCase()} by ${chartData.exchange}",
                    NotificationManager.IMPORTANCE_MIN
            )

            val manager = context.getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(serviceChannel)
        }
    }

    /**
     * Remove notification channel
     */
    fun deleteNotificationChannel(chartData: ChartData, context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(NotificationManager::class.java)
            manager!!.deleteNotificationChannel("${chartData.coin}/${chartData.currency} ${chartData.id}")
        }
    }

    /**
     * Remove Ongoing notification
     */
    fun clearOngoinNotification(chartData: ChartData, context: Context) {
        if (!chartData.isNotificationEnabled) {
            val mNotificationManager = NotificationManagerCompat.from(context)
            mNotificationManager.cancel(chartData.id.toInt())
        }
    }

    fun calculateInterval(intervalUpdateIndex: Int): Long {
        when (intervalUpdateIndex) {
            0 -> {
                return 5
            }
            1 -> {
                return 10
            }
            2 -> {
                return 15
            }
            3 -> {
                return 30
            }
            4 -> {
                return 60
            }
            5 -> {
                return 60 * 2
            }
            6 -> {
                return 60 * 5
            }
            7 -> {
                return 60 * 10
            }
            8 -> {
                return 60 * 15
            }
            9 -> {
                return 60 * 30
            }
            10 -> {
                return 60 * 60
            }
            11 -> {
                return 60 * 60 * 2
            }
            12 -> {
                return 60 * 60 * 5
            }
            13 -> {
                return 60 * 60 * 12
            }
            14 -> {
                return 60 * 60 * 24
            }
            15 -> {
                return 60 * 60 * 48
            }
            16 -> {
                return 60 * 60 * 48
            }
            17 -> {
                return 60 * 60 * 168
            }
            18 -> {
                return 60 * 60 * 336
            }
        }
        return 1000
    }
}
