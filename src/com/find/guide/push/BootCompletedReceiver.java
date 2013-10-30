package com.find.guide.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

@Deprecated
public class BootCompletedReceiver extends BroadcastReceiver{

    public static final String ACTION_BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";
    @Override
    public void onReceive(Context context, Intent intent) {
        
    }

}
