/**
 * NotificationHelper.java
 */
package com.find.guide.push;

import com.find.guide.R;
import com.find.guide.activity.MainActivity;
import com.find.guide.guide.GuideEvent;
import com.find.guide.invite.InviteEvent;
import com.find.guide.user.Tourist;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class NotificationHelper {

    public static int sNewMsgId = 0;

    private static NotificationHelper mInstance;
    private Context mContext;
    private NotificationManager mNotificationManager;

    public synchronized static NotificationHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new NotificationHelper(context);
        }
        return mInstance;
    }

    private NotificationHelper(Context context) {
        mContext = context.getApplicationContext();
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void notifyMessage(Message msg) {
        Notification notification = makeNotification(msg);
        mNotificationManager.notify(sNewMsgId++, notification);
    }

    public Notification makeNotification(Message msg) {
        int flag = Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext).setSmallIcon(R.drawable.icon)
                .setDefaults(flag).setAutoCancel(true);

        builder.setContentTitle(mContext.getString(R.string.app_name));
        builder.setContentText(msg.content);
        builder.setTicker(msg.content);

        Intent intent = new Intent(mContext, MainActivity.class);
        intent.setAction(MainActivity.ACTION_PUSH_CLICK);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        return builder.build();
    }

    @Deprecated
    public void notifyGuideEvent(GuideEvent event) {
        Notification notification = makeNotification(Tourist.USER_TYPE_TOURGUIDE, event);
        mNotificationManager.notify(sNewMsgId++, notification);
    }

    @Deprecated
    public void notifyInviteEvent(InviteEvent event) {
        Notification notification = makeNotification(Tourist.USER_TYPE_TOURIST, event);
        mNotificationManager.notify(sNewMsgId++, notification);
    }

    @Deprecated
    public Notification makeNotification(int userType, Object obj) {
        int flag = Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext).setSmallIcon(R.drawable.icon)
                .setDefaults(flag).setAutoCancel(true);

        builder.setContentTitle(mContext.getString(R.string.app_name));
        String content = null;
        if (userType == Tourist.USER_TYPE_TOURGUIDE) {
            GuideEvent event = (GuideEvent) obj;
            if (event.getEventStatus() == GuideEvent.EVENT_STATUS_WAITING) {
                if (event.getEventType() == GuideEvent.EVENT_TYPE_BROADCASR) {
                    content = event.getUserName() + " 发起了一个广播";
                } else {
                    content = event.getUserName() + " 向您发起了预约";
                }
            } else if (event.getEventStatus() == GuideEvent.EVENT_STATUS_CANCEL) {
                content = event.getUserName() + " 取消了预约";
            } else if (event.getEventStatus() == GuideEvent.EVENT_STATUS_SATISFACTION) {
                content = event.getUserName() + " 对您进行了评价";
            }
        } else {
            InviteEvent event = (InviteEvent) obj;
            if (event.getEventStatus() == InviteEvent.EVENT_STATUS_ACCEPTED) {
                content = event.getGuideName() + " 接受了您的预约";
            } else if (event.getEventStatus() == InviteEvent.EVENT_STATUS_REFUSED) {
                content = event.getGuideName() + " 拒绝了您的预约";
            }
        }
        builder.setContentText(content);
        builder.setTicker(content);

        Intent intent = new Intent(mContext, MainActivity.class);
        intent.setAction(MainActivity.ACTION_PUSH_CLICK);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        return builder.build();
    }

    public void cancelAll() {
        sNewMsgId = 0;
        mNotificationManager.cancelAll();
    }

}
