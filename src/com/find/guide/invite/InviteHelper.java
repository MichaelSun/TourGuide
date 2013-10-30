package com.find.guide.invite;

import java.util.List;

import android.content.Context;

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
import com.find.guide.setting.SettingManager;
import com.plugin.common.utils.CustomThreadPool;
import com.plugin.internet.InternetUtils;
import com.plugin.internet.core.NetWorkException;

public class InviteHelper {

    public static final int SUCCESS = 0;
    public static final int FAILED = -1;
    public static final int NETWORK_ERROR = -2;

    private Context mContext;

    private OnGetHistoricalInviteEventsListener mOnGetHistoricalInviteEventsListener;
    private OnSetStatisfactionListener mOnSetStatisfactionListener = null;
    private OnInviteListener mOnInviteListener = null;

    public InviteHelper(Context context) {
        mContext = context.getApplicationContext();
    }

    public static interface OnGetHistoricalInviteEventsListener {
        public void onGetHistoricalInviteEvents(int result, List<InviteEvent> inviteEvents);

        public void onGetMoreHistoricalInviteEvents(int result, List<InviteEvent> inviteEvents);
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
                        if (start == 0 && inviteEvents != null && inviteEvents.size() > 0) {
                            SettingManager.getInstance().setMaxEventId(inviteEvents.get(0).getEventId());
                            SettingManager.getInstance().setMaxEventStatus(inviteEvents.get(0).getEventStatus());
                        }
                        if (mOnGetHistoricalInviteEventsListener != null) {
                            if (start > 0) {
                                mOnGetHistoricalInviteEventsListener.onGetMoreHistoricalInviteEvents(SUCCESS,
                                        inviteEvents);
                            } else {
                                mOnGetHistoricalInviteEventsListener.onGetHistoricalInviteEvents(SUCCESS, inviteEvents);
                            }
                        }
                        return;
                    }
                } catch (NetWorkException e) {
                    e.printStackTrace();
                }

                if (mOnGetHistoricalInviteEventsListener != null) {
                    if (start > 0) {
                        mOnGetHistoricalInviteEventsListener.onGetMoreHistoricalInviteEvents(NETWORK_ERROR, null);
                    } else {
                        mOnGetHistoricalInviteEventsListener.onGetHistoricalInviteEvents(NETWORK_ERROR, null);
                    }
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
                    if (response != null) {
                        if (response.result == 0) {
                            if (mOnInviteListener != null) {
                                mOnInviteListener.onInviteAll(SUCCESS);
                            }
                        } else {
                            if (mOnInviteListener != null) {
                                mOnInviteListener.onInviteAll(FAILED);
                            }
                        }
                        return;
                    }
                } catch (NetWorkException e) {
                    e.printStackTrace();
                }
                if (mOnInviteListener != null) {
                    mOnInviteListener.onInviteAll(NETWORK_ERROR);
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
                    if (response != null) {
                        if (response.result == 0) {
                            if (mOnInviteListener != null) {
                                mOnInviteListener.onCancelInvite(SUCCESS);
                            }
                        } else {
                            if (mOnInviteListener != null) {
                                mOnInviteListener.onCancelInvite(FAILED);
                            }
                        }
                        return;
                    }
                } catch (NetWorkException e) {
                    e.printStackTrace();
                }
                if (mOnInviteListener != null) {
                    mOnInviteListener.onCancelInvite(NETWORK_ERROR);
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
                    if (response != null) {
                        if (response.result == 0) {
                            if (mOnInviteListener != null) {
                                mOnInviteListener.onInvite(SUCCESS);
                            }
                        } else {
                            if (mOnInviteListener != null) {
                                mOnInviteListener.onInvite(FAILED);
                            }
                        }
                        return;
                    }
                } catch (NetWorkException e) {
                    e.printStackTrace();
                }
                if (mOnInviteListener != null) {
                    mOnInviteListener.onInvite(NETWORK_ERROR);
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
                    if (response != null) {
                        if (response.result == 0) {
                            if (mOnSetStatisfactionListener != null) {
                                mOnSetStatisfactionListener.onSetStatisfaction(SUCCESS);
                            }
                        } else {
                            if (mOnSetStatisfactionListener != null) {
                                mOnSetStatisfactionListener.onSetStatisfaction(FAILED);
                            }
                        }
                        return;
                    }
                } catch (NetWorkException e) {
                    e.printStackTrace();
                }
                if (mOnSetStatisfactionListener != null) {
                    mOnSetStatisfactionListener.onSetStatisfaction(NETWORK_ERROR);
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
