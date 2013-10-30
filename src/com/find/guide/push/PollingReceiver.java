package com.find.guide.push;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import com.find.guide.api.guide.GetHistoricalGuideEventsRequest;
import com.find.guide.api.guide.GetHistoricalGuideEventsResponse;
import com.find.guide.api.invite.GetHistoricalInviteEventsRequest;
import com.find.guide.api.invite.GetHistoricalInviteEventsResponse;
import com.find.guide.app.TourGuideApplication;
import com.find.guide.config.AppConfig;
import com.find.guide.guide.GuideEvent;
import com.find.guide.invite.InviteEvent;
import com.find.guide.setting.SettingManager;
import com.find.guide.user.Tourist;
import com.plugin.internet.InternetUtils;
import com.plugin.internet.core.NetWorkException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

@Deprecated
public class PollingReceiver extends BroadcastReceiver {

    public static final String ACTION_POLLING = "com.find.guide.message.POLLING";

    private ExecutorService mPollingExecutor = Executors.newSingleThreadExecutor();

    private AtomicBoolean mIsPolling = new AtomicBoolean(false);

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && ACTION_POLLING.equals(intent.getAction())) {
            if (mIsPolling.get())
                return;
            mIsPolling.set(true);

            mPollingExecutor.execute(mPollingRunnable);
        }
    }

    private Runnable mPollingRunnable = new Runnable() {

        @Override
        public void run() {
            SettingManager settingManager = SettingManager.getInstance();
            if (settingManager.getUserId() > 0) {
                AppConfig.LOGD("PollingReceiver --> start polling");
                if (settingManager.getUserType() == Tourist.USER_TYPE_TOURGUIDE && settingManager.getGuideMode() == 0) {
                    getGuideEvent();
                } else {
                    getInviteEvent();
                }
            }
            mIsPolling.set(false);
        }
    };

    private void getGuideEvent() {
        try {
            GetHistoricalGuideEventsRequest request = new GetHistoricalGuideEventsRequest(0, 0, 5);
            request.setIgnoreResult(true);
            GetHistoricalGuideEventsResponse response = InternetUtils.request(TourGuideApplication.getInstance(),
                    request);
            if (response != null && response.guideEvents != null) {
                long maxEventId = SettingManager.getInstance().getMaxEventId();
                for (int i = response.guideEvents.size() - 1; i >= 0; i--) {
                    GuideEvent event = response.guideEvents.get(i);
                    if (event.getEventId() > maxEventId
                            && (event.getEventStatus() == GuideEvent.EVENT_STATUS_WAITING
                                    || event.getEventStatus() == GuideEvent.EVENT_STATUS_CANCEL || event
                                    .getEventStatus() == GuideEvent.EVENT_STATUS_SATISFACTION)) {
                        maxEventId = event.getEventId();
                        NotificationHelper.getInstance(TourGuideApplication.getInstance()).notifyGuideEvent(event);
                    }
                }
                SettingManager.getInstance().setMaxEventId(maxEventId);
            }
        } catch (NetWorkException e) {
            e.printStackTrace();
        }
    }

    private void getInviteEvent() {
        try {
            GetHistoricalInviteEventsRequest request = new GetHistoricalInviteEventsRequest(0, 5);
            request.setIgnoreResult(true);
            GetHistoricalInviteEventsResponse response = InternetUtils.request(TourGuideApplication.getInstance(),
                    request);
            if (response != null && response.inviteEvents != null) {
                long maxEventId = SettingManager.getInstance().getMaxEventId();
                for (int i = response.inviteEvents.size() - 1; i >= 0; i--) {
                    InviteEvent event = response.inviteEvents.get(i);
                    if (event.getEventId() > maxEventId
                            && (event.getEventStatus() == InviteEvent.EVENT_STATUS_ACCEPTED || event.getEventStatus() == InviteEvent.EVENT_STATUS_REFUSED)) {
                        maxEventId = event.getEventId();
                        NotificationHelper.getInstance(TourGuideApplication.getInstance()).notifyInviteEvent(event);
                    }
                }
                SettingManager.getInstance().setMaxEventId(maxEventId);
            }
        } catch (NetWorkException e) {
            e.printStackTrace();
        }
    }

}
