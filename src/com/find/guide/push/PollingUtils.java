package com.find.guide.push;

import com.find.guide.config.AppConfig;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

@Deprecated
public class PollingUtils {

    // 开启轮询
    public static void startPolling(Context context) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent();
        intent.setAction(PollingReceiver.ACTION_POLLING);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        long triggerAtTime = SystemClock.elapsedRealtime();
        manager.setRepeating(AlarmManager.ELAPSED_REALTIME, triggerAtTime, AppConfig.POLLING_INTERAL_MILLIS,
                pendingIntent);
    }

    // 停止轮询
    public static void stopPolling(Context context) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent();
        intent.setAction(PollingReceiver.ACTION_POLLING);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        manager.cancel(pendingIntent);
    }
}
