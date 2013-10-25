package com.find.guide.config;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.find.guide.activity.MainActivity;
import com.find.guide.app.TourGuideApplication;
import com.find.guide.setting.SettingManager;
import com.find.guide.utils.XMLTables;
import com.plugin.common.utils.CustomThreadPool;
import com.plugin.common.utils.DeviceInfo;
import com.plugin.common.utils.UtilsConfig;
import com.plugin.common.utils.files.DiskManager;

public class AppRuntime {

    public static final class AppDeviceInfo {

        // public final String buildInfo;

        public DeviceInfo deviceInfo;

        public AppDeviceInfo(Context context) {
            // String build = context.getString(R.string.build_code);
            // String server_code = context.getString(R.string.server_code);
            // String release_code = context.getString(R.string.release_code);

            // if (AppConfig.DEBUG_ONLINE_SERVER) {
            // server_code = "2";
            // } else if (AppConfig.DEBUG_DINGBAN_SERVER) {
            // server_code = "1";
            // } else {
            // server_code = "0";
            // }
            //
            // if (AppConfig.DEBUG) {
            // release_code = "0";
            // } else {
            // release_code = "1";
            // }

            // buildInfo = build + server_code + release_code;
            deviceInfo = UtilsConfig.DEVICE_INFO;
        }

    }

    public static String gLocation = null;

    public static XMLTables gXMLTables;

    public static AppDeviceInfo APP_DEVICE_INFO;

    public static boolean PROCESS_IS_RUNNING;

    public static AtomicBoolean gCleanTaskRunning = new AtomicBoolean(false);

    public static AtomicBoolean gInLogoutProcess = new AtomicBoolean(false);

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = (cm != null) ? cm.getActiveNetworkInfo() : null;
        if (info != null && info.isAvailable() && info.isConnected()) {
            return true;
        }

        return false;
    }

    public static boolean sIsForground = false;

    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(1);
        if (tasks != null && !tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    public synchronized static void tryToCleanEnvOnDestroy() {
        synchronized (gCleanTaskRunning) {
            if (!gCleanTaskRunning.get()) {
                gCleanTaskRunning.set(true);
                CustomThreadPool.asyncWork(new Runnable() {
                    @Override
                    public void run() {
                        DiskManager.tryToCleanDisk();
                        gCleanTaskRunning.set(false);
                    }
                });
            }
        }
    }

    public static void sendKickoutIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(TourGuideApplication.ACTION_KICKOUT);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void logout() {
        SettingManager.getInstance().clearAll();
    }

}
