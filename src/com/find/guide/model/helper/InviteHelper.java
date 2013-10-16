package com.find.guide.model.helper;

import java.util.ArrayList;
import java.util.List;

import com.find.guide.api.invite.GetHistoricalInviteEventsRequest;
import com.find.guide.api.invite.GetHistoricalInviteEventsResponse;
import com.find.guide.api.invite.InviteAllRequest;
import com.find.guide.api.invite.InviteAllResponse;
import com.find.guide.api.invite.InviteCancelRequest;
import com.find.guide.api.invite.InviteCancelResponse;
import com.find.guide.api.invite.InviteRequest;
import com.find.guide.api.invite.InviteResponse;
import com.find.guide.api.invite.SetSatisfactionRequest;
import com.find.guide.api.invite.SetSatisfactionResponse;
import com.find.guide.config.AppConfig;
import com.find.guide.model.InviteEvent;
import com.plugin.common.utils.CustomThreadPool;
import com.plugin.internet.InternetUtils;
import com.plugin.internet.core.NetWorkException;

import android.content.Context;

public class InviteHelper {

    private static final boolean TEST_DATA = true & AppConfig.DEBUG;

    public static final int SUCCESS = 0;
    public static final int FAILED = -1;

    private Context mContext;

    private OnGetHistoricalInviteEventsListener mOnGetHistoricalInviteEventsListener;
    private OnSetStatisfactionListener mOnSetStatisfactionListener = null;
    private OnInviteListener mOnInviteListener = null;

    public InviteHelper(Context context) {
        mContext = context.getApplicationContext();
    }

    public static interface OnGetHistoricalInviteEventsListener {
        public void onGetHistoricalInviteEvents(int result, List<InviteEvent> inviteEvents);
    }

    public void getHistoricalInviteEvents(final int start, final int rows, OnGetHistoricalInviteEventsListener listener) {
        mOnGetHistoricalInviteEventsListener = listener;

        CustomThreadPool.asyncWork(new Runnable() {
            @Override
            public void run() {
                try {
                    GetHistoricalInviteEventsRequest request = new GetHistoricalInviteEventsRequest(start, rows);
                    GetHistoricalInviteEventsResponse response = InternetUtils.request(mContext, request);
                    if (response != null) {
                        List<InviteEvent> inviteEvents = response.inviteEvents;
                        // test
                        if (TEST_DATA && (inviteEvents == null || inviteEvents.size() == 0)) {
                            if (inviteEvents == null) {
                                inviteEvents = new ArrayList<InviteEvent>();
                            }
                            for (int i = 0; i < 20; i++) {
                                InviteEvent inviteEvent = new InviteEvent(1000000 + i, 100000 + i, 1200 + i, i % 2,
                                        i % 5, 12308612812l, 1229310231l, "颐和园", 12329102301l, i % 2);
                                inviteEvents.add(inviteEvent);
                            }
                        }
                        if (mOnGetHistoricalInviteEventsListener != null) {
                            mOnGetHistoricalInviteEventsListener.onGetHistoricalInviteEvents(SUCCESS, inviteEvents);
                        }
                        return;
                    }
                } catch (NetWorkException e) {
                    e.printStackTrace();
                }

                if (TEST_DATA) {
                    List<InviteEvent> inviteEvents = new ArrayList<InviteEvent>();
                    for (int i = 0; i < 20; i++) {
                        InviteEvent inviteEvent = new InviteEvent(1000000 + i, 100000 + i, 1200 + i, i % 2, i % 5,
                                12308612812l, 1229310231l, "颐和园", 12329102301l, i % 2);
                        inviteEvents.add(inviteEvent);
                    }
                    if (mOnGetHistoricalInviteEventsListener != null) {
                        mOnGetHistoricalInviteEventsListener.onGetHistoricalInviteEvents(SUCCESS, inviteEvents);
                    }
                    return;
                }

                if (mOnGetHistoricalInviteEventsListener != null) {
                    mOnGetHistoricalInviteEventsListener.onGetHistoricalInviteEvents(FAILED, null);
                }
            }
        });
    }

    public void getOneInviteEvent() {

    }

    public static interface OnInviteListener {
        public void onInviteAll(int result);

        public void onInvite(int result);

        public void onCancelInvite(int result);
    }

    public void setOnInviteListener(OnInviteListener listener) {
        mOnInviteListener = listener;
    }

    public void inviteAll(final String scenic, final long startTime, final long endTime, final String location,
            final int gender) {
        CustomThreadPool.asyncWork(new Runnable() {
            @Override
            public void run() {
                try {
                    InviteAllRequest request = new InviteAllRequest(scenic, startTime, endTime, location, gender);
                    InviteAllResponse response = InternetUtils.request(mContext, request);
                    if (response != null && response.result == 0) {
                        if (mOnInviteListener != null) {
                            mOnInviteListener.onInviteAll(SUCCESS);
                        }
                        return;
                    }
                } catch (NetWorkException e) {
                    e.printStackTrace();
                }
                if (mOnInviteListener != null) {
                    mOnInviteListener.onInviteAll(FAILED);
                }
            }
        });
    }

    public void cancelInvite(final long eventId, final int guideId) {
        CustomThreadPool.asyncWork(new Runnable() {
            @Override
            public void run() {
                try {
                    InviteCancelRequest request = new InviteCancelRequest(eventId, guideId);
                    InviteCancelResponse response = InternetUtils.request(mContext, request);
                    if (response != null && response.result == 0) {
                        if (mOnInviteListener != null) {
                            mOnInviteListener.onCancelInvite(SUCCESS);
                        }
                        return;
                    }
                } catch (NetWorkException e) {
                    e.printStackTrace();
                }
                if (mOnInviteListener != null) {
                    mOnInviteListener.onCancelInvite(FAILED);
                }
            }
        });
    }

    public void invite(final int guideId, final String scenic, final long startTime, final long endTime) {
        CustomThreadPool.asyncWork(new Runnable() {
            @Override
            public void run() {
                try {
                    InviteRequest request = new InviteRequest(guideId, scenic, startTime, endTime);
                    InviteResponse response = InternetUtils.request(mContext, request);
                    if (response != null && response.result == 0) {
                        if (mOnInviteListener != null) {
                            mOnInviteListener.onInvite(SUCCESS);
                        }
                        return;
                    }
                } catch (NetWorkException e) {
                    e.printStackTrace();
                }
                if (mOnInviteListener != null) {
                    mOnInviteListener.onInvite(FAILED);
                }
            }
        });
    }

    public static interface OnSetStatisfactionListener {
        public void onSetStatisfaction(int result);
    }

    public void setStatisfaction(final long eventId, final int guideId, final int satisfaction,
            OnSetStatisfactionListener listener) {
        mOnSetStatisfactionListener = listener;

        CustomThreadPool.asyncWork(new Runnable() {
            @Override
            public void run() {
                try {
                    SetSatisfactionRequest request = new SetSatisfactionRequest(eventId, guideId, satisfaction);
                    SetSatisfactionResponse response = InternetUtils.request(mContext, request);
                    if (response != null && response.result == 0) {
                        if (mOnSetStatisfactionListener != null) {
                            mOnSetStatisfactionListener.onSetStatisfaction(SUCCESS);
                        }
                        return;
                    }
                } catch (NetWorkException e) {
                    e.printStackTrace();
                }
                if (mOnSetStatisfactionListener != null) {
                    mOnSetStatisfactionListener.onSetStatisfaction(FAILED);
                }
            }
        });
    }

    public void destroy() {
        mContext = null;
        mOnSetStatisfactionListener = null;
        mOnInviteListener = null;
        mOnGetHistoricalInviteEventsListener = null;
    }
}
