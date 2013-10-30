package com.find.guide.push;

import com.find.guide.api.push.BindDeviceRequest;
import com.find.guide.api.push.UnbindDeviceRequest;
import com.find.guide.api.push.UnbindDeviceResponse;
import com.plugin.common.utils.CustomThreadPool;
import com.plugin.internet.InternetUtils;
import com.plugin.internet.core.NetWorkException;

import android.content.Context;

public class PushHelper {

    public static final int SUCCESS = 0;
    public static final int FAILED = -1;
    public static final int NETWORK_ERROR = -2;

    private Context mContext;

    public UnbindDeviceListener mUnbindDeviceListener = null;

    public PushHelper(Context context) {
        mContext = context.getApplicationContext();
    }

    public void bindDevice(final String channelId, final String userId) {
        CustomThreadPool.asyncWork(new Runnable() {
            @Override
            public void run() {
                try {
                    BindDeviceRequest request = new BindDeviceRequest(channelId, userId);
                    request.setIgnoreResult(true);
                    InternetUtils.request(mContext, request);
                } catch (NetWorkException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public static interface UnbindDeviceListener {
        public void unbindDevice(int result);
    }

    public void AsyncUnbindDevice(UnbindDeviceListener listener) {
        mUnbindDeviceListener = listener;
        CustomThreadPool.asyncWork(new Runnable() {
            @Override
            public void run() {
                try {
                    UnbindDeviceRequest request = new UnbindDeviceRequest();
                    UnbindDeviceResponse response = InternetUtils.request(mContext, request);
                    if (response != null) {
                        if (response.result == 0) {
                            if (mUnbindDeviceListener != null) {
                                mUnbindDeviceListener.unbindDevice(SUCCESS);
                            }
                        } else {
                            if (mUnbindDeviceListener != null) {
                                mUnbindDeviceListener.unbindDevice(FAILED);
                            }
                        }
                    }
                } catch (NetWorkException e) {
                    e.printStackTrace();
                }
                if (mUnbindDeviceListener != null) {
                    mUnbindDeviceListener.unbindDevice(NETWORK_ERROR);
                }
            }
        });
    }

    public void destroy() {
        mUnbindDeviceListener = null;
        mContext = null;
    }
}
