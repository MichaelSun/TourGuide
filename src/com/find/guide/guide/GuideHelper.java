package com.find.guide.guide;

import java.util.List;

import android.content.Context;

import com.find.guide.api.guide.GetHistoricalGuideEventsRequest;
import com.find.guide.api.guide.GetHistoricalGuideEventsResponse;
import com.find.guide.api.guide.GuideAcceptRequest;
import com.find.guide.api.guide.GuideAcceptResponse;
import com.find.guide.api.guide.GuideRefuseRequest;
import com.find.guide.api.guide.GuideRefuseResponse;
import com.find.guide.setting.SettingManager;
import com.plugin.common.utils.CustomThreadPool;
import com.plugin.internet.InternetUtils;
import com.plugin.internet.core.NetWorkException;

public class GuideHelper {

    public static final int SUCCESS = 0;
    public static final int FAILED = -1;
    public static final int NETWORK_ERROR = -2;

    private Context mContext;

    private OnGetHistoricalGuideEventsListener mOnGetHistoricalGuideEventsListener = null;
    private OnAcceptedListener mOnAcceptedListener = null;
    private OnRefusedListener mOnRefusedListener = null;

    public GuideHelper(Context context) {
        mContext = context.getApplicationContext();
    }

    public static interface OnGetHistoricalGuideEventsListener {
        public void onGetHistoricalGuideEvents(int result, List<GuideEvent> guideEvents);

        public void onGetMoreHistoricalGuideEvents(int result, List<GuideEvent> guideEvents);
    }

    public void getHistoricalGuideEvents(final int userId, final int start, final int rows,
            OnGetHistoricalGuideEventsListener listener) {
        mOnGetHistoricalGuideEventsListener = listener;

        CustomThreadPool.asyncWork(new Runnable() {
            @Override
            public void run() {
                try {
                    GetHistoricalGuideEventsRequest request = new GetHistoricalGuideEventsRequest(userId, start, rows);
                    GetHistoricalGuideEventsResponse response = InternetUtils.request(mContext, request);
                    if (response != null) {
                        List<GuideEvent> guideEvents = response.guideEvents;
                        if (start == 0 && guideEvents != null && guideEvents.size() > 0) {
                            SettingManager.getInstance().setMaxEventId(guideEvents.get(0).getEventId());
                            SettingManager.getInstance().setMaxEventStatus(guideEvents.get(0).getEventStatus());
                        }
                        if (mOnGetHistoricalGuideEventsListener != null) {
                            if (start > 0) {
                                mOnGetHistoricalGuideEventsListener
                                        .onGetMoreHistoricalGuideEvents(SUCCESS, guideEvents);
                            } else {
                                mOnGetHistoricalGuideEventsListener.onGetHistoricalGuideEvents(SUCCESS, guideEvents);
                            }
                        }
                        return;
                    }
                } catch (NetWorkException e) {
                    e.printStackTrace();
                }

                if (mOnGetHistoricalGuideEventsListener != null) {
                    if (start > 0) {
                        mOnGetHistoricalGuideEventsListener.onGetMoreHistoricalGuideEvents(NETWORK_ERROR, null);
                    } else {
                        mOnGetHistoricalGuideEventsListener.onGetHistoricalGuideEvents(NETWORK_ERROR, null);
                    }
                }
            }
        });
    }

    public static interface OnAcceptedListener {
        public void onAccepted(int result);
    }

    public void accept(final long eventId, final int userId, OnAcceptedListener listener) {
        mOnAcceptedListener = listener;

        CustomThreadPool.asyncWork(new Runnable() {
            @Override
            public void run() {
                try {
                    GuideAcceptRequest request = new GuideAcceptRequest(eventId, userId);
                    GuideAcceptResponse response = InternetUtils.request(mContext, request);
                    if (response != null) {
                        if (response.result == 0) {
                            if (mOnAcceptedListener != null) {
                                mOnAcceptedListener.onAccepted(SUCCESS);
                            }
                        } else {
                            if (mOnAcceptedListener != null) {
                                mOnAcceptedListener.onAccepted(FAILED);
                            }
                        }
                        return;
                    }
                } catch (NetWorkException e) {
                    e.printStackTrace();
                }
                if (mOnAcceptedListener != null) {
                    mOnAcceptedListener.onAccepted(NETWORK_ERROR);
                }
            }
        });
    }

    public static interface OnRefusedListener {
        public void onRefused(int result);
    }

    public void refuse(final long eventId, final int userId, OnRefusedListener listener) {
        mOnRefusedListener = listener;

        CustomThreadPool.asyncWork(new Runnable() {
            @Override
            public void run() {
                try {
                    GuideRefuseRequest request = new GuideRefuseRequest(eventId, userId);
                    GuideRefuseResponse response = InternetUtils.request(mContext, request);
                    if (response != null) {
                        if (response.result == 0) {
                            if (mOnRefusedListener != null) {
                                mOnRefusedListener.onRefused(SUCCESS);
                            }
                        } else {
                            if (mOnRefusedListener != null) {
                                mOnRefusedListener.onRefused(FAILED);
                            }
                        }
                        return;
                    }
                } catch (NetWorkException e) {
                    e.printStackTrace();
                }
                if (mOnRefusedListener != null) {
                    mOnRefusedListener.onRefused(NETWORK_ERROR);
                }
            }
        });
    }

    public void destroy() {
        mContext = null;
        mOnGetHistoricalGuideEventsListener = null;
        mOnAcceptedListener = null;
        mOnRefusedListener = null;
    }
}
