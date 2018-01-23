package workshop.akbolatss.xchangesrates.screens.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Author: Akbolat Sadvakassov
 * Date: 22.01.2018
 */

public class NotificationReceiver extends BroadcastReceiver {

    private static final String TAG = "NotificationReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, NotificationService.class));
        Log.d(TAG, "onReceive");
    }
}
