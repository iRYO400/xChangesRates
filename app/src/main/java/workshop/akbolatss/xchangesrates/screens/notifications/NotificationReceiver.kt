package workshop.akbolatss.xchangesrates.screens.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import workshop.akbolatss.xchangesrates.utils.Logger
import workshop.akbolatss.xchangesrates.utils.UtilityMethods

/**
 * Author: Akbolat Sadvakassov
 * Date: 22.01.2018
 */

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Logger.i("onReceive broadcast")
        val mServiceIntent = Intent(context, NotificationService::class.java)
        context.startService(mServiceIntent)
    }
}
