package com.find.guide.model;

import com.find.guide.api.GetVerifyCodeRequest;
import com.find.guide.api.GetVerifyCodeResponse;
import com.find.guide.api.LoginRequest;
import com.find.guide.api.LoginResponse;
import com.find.guide.api.RegisterRequest;
import com.find.guide.api.RegisterResponse;
import com.find.guide.setting.SettingManager;
import com.plugin.common.utils.CustomThreadPool;
import com.plugin.internet.InternetUtils;
import com.plugin.internet.core.NetWorkException;

import android.content.Context;

public class LoginHelper {

    public static final int LOGIN_SUCCESS = 0;
    public static final int LOGIN_FAILED = -1;

    public static final int REGISTER_SUCCESS = 0;
    public static final int REGISTER_FAILED = -1;

    public static final int GET_VERIFY_CODE_SUCCESS = 0;
    public static final int GET_VERIFY_CODE_FAILED = -1;

    private OnLoginFinishListener mOnLoginFinishListener = null;
    private OnRegisterFinishListener mOnRegisterFinishListener = null;
    private OnGetVerifyCodeFinishListener mOnGetVerifyCodeFinishListener = null;

    private Context mContext;

    public LoginHelper(Context context) {
        mContext = context.getApplicationContext();
    }

    public static interface OnLoginFinishListener {
        public void onLoginFinish(int result);
    }

    public void login(final String mobileNum, final String password, OnLoginFinishListener listener) {
        mOnLoginFinishListener = listener;

        CustomThreadPool.asyncWork(new Runnable() {
            @Override
            public void run() {
                try {
                    LoginRequest request = new LoginRequest(mobileNum, password);
                    LoginResponse response = InternetUtils.request(mContext, request);
                    if (response != null && response.userId > 0) {
                        SettingManager.getInstance().setUserId(response.userId);
                        SettingManager.getInstance().setTicket(response.ticket);
                        SettingManager.getInstance().setSecretKey(response.userSecretKey);

                        if (mOnLoginFinishListener != null) {
                            mOnLoginFinishListener.onLoginFinish(LOGIN_SUCCESS);
                        }
                        return;
                    }
                } catch (NetWorkException e) {
                    e.printStackTrace();
                }

                if (mOnLoginFinishListener != null) {
                    mOnLoginFinishListener.onLoginFinish(LOGIN_FAILED);
                }
            }
        });
    }

    public static interface OnRegisterFinishListener {
        public void onRegisterFinish(int result);
    }

    public void register(final String mobileNum, final String password, final String name, final String verifyCode,
            final int gender, OnRegisterFinishListener listener) {
        mOnRegisterFinishListener = listener;

        CustomThreadPool.asyncWork(new Runnable() {
            @Override
            public void run() {
                try {
                    RegisterRequest request = new RegisterRequest(mobileNum, password, name, verifyCode, gender);
                    RegisterResponse response = InternetUtils.request(mContext, request);
                    if (response != null && response.userId > 0) {
                        SettingManager.getInstance().setUserId(response.userId);
                        SettingManager.getInstance().setTicket(response.ticket);
                        SettingManager.getInstance().setSecretKey(response.userSecretKey);

                        if (mOnRegisterFinishListener != null) {
                            mOnRegisterFinishListener.onRegisterFinish(REGISTER_SUCCESS);
                        }
                        return;
                    }
                } catch (NetWorkException e) {
                    e.printStackTrace();
                }

                if (mOnRegisterFinishListener != null) {
                    mOnRegisterFinishListener.onRegisterFinish(REGISTER_FAILED);
                }
            }
        });
    }

    public static interface OnGetVerifyCodeFinishListener {
        public void onGetVerifyCodeFinish(int result);
    }

    public void getVerifyCode(final String mobile, OnGetVerifyCodeFinishListener listener) {
        mOnGetVerifyCodeFinishListener = listener;

        CustomThreadPool.asyncWork(new Runnable() {
            @Override
            public void run() {
                try {
                    GetVerifyCodeRequest request = new GetVerifyCodeRequest(mobile);
                    GetVerifyCodeResponse response = InternetUtils.request(mContext, request);
                    if (response != null && response.result == GetVerifyCodeResponse.SUCCESS) {
                        if (mOnGetVerifyCodeFinishListener != null) {
                            mOnGetVerifyCodeFinishListener.onGetVerifyCodeFinish(GET_VERIFY_CODE_SUCCESS);
                        }
                        return;
                    }
                } catch (NetWorkException e) {
                    e.printStackTrace();
                }

                if (mOnGetVerifyCodeFinishListener != null) {
                    mOnGetVerifyCodeFinishListener.onGetVerifyCodeFinish(GET_VERIFY_CODE_FAILED);
                }
            }
        });
    }

    public void destroy() {
        mContext = null;
        mOnLoginFinishListener = null;
        mOnRegisterFinishListener = null;
        mOnGetVerifyCodeFinishListener = null;
    }
}
