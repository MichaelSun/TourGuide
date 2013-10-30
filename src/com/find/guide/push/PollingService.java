package com.find.guide.push;

import com.find.guide.config.AppConfig;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

@Deprecated
public class PollingService extends Service {

    private static final String ACTION_START = "action_start";
    private static final String ACTION_STOP = "action_stop";

    private BroadcastReceiver mPollingReceiver = new PollingReceiver();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void startService(Context ctx) {
        Intent i = new Intent();
        i.setClass(ctx, PollingService.class);
        i.setAction(ACTION_START);
        ctx.startService(i);
    }

    public static void stopService(Context ctx) {
        Intent i = new Intent();
        i.setClass(ctx, PollingService.class);
        i.setAction(ACTION_STOP);
        ctx.startService(i);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppConfig.LOGD("PollingService --> onCreate");
        registerReceiver(mPollingReceiver, new IntentFilter(PollingReceiver.ACTION_POLLING));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            String action = intent.getAction();
            if (action != null && action.equals(ACTION_START)) {
                // PollingUtils.stopPolling(getApplicationContext());
                // PollingUtils.startPolling(getApplicationContext());
            } else if (action != null && action.equals(ACTION_STOP)) {
                // PollingUtils.stopPolling(getApplicationContext());
                stopSelf();
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mPollingReceiver);
    }

}
