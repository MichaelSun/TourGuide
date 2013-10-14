package com.find.guide.model;

import com.find.guide.api.user.ApplyForGuideRequest;
import com.find.guide.api.user.ApplyForGuideResponse;
import com.find.guide.api.user.GetUserInfoRequest;
import com.find.guide.api.user.GetUserInfoResponse;
import com.find.guide.api.user.GetVerifyCodeRequest;
import com.find.guide.api.user.GetVerifyCodeResponse;
import com.find.guide.api.user.LoginRequest;
import com.find.guide.api.user.LoginResponse;
import com.find.guide.api.user.RegisterRequest;
import com.find.guide.api.user.RegisterResponse;
import com.find.guide.setting.SettingManager;
import com.plugin.common.utils.CustomThreadPool;
import com.plugin.internet.InternetUtils;
import com.plugin.internet.core.NetWorkException;

import android.content.Context;

public class UserHelper {

    public static final int LOGIN_SUCCESS = 0;
    public static final int LOGIN_FAILED = -1;

    public static final int REGISTER_SUCCESS = 0;
    public static final int REGISTER_FAILED = -1;

    public static final int GET_VERIFY_CODE_SUCCESS = 0;
    public static final int GET_VERIFY_CODE_FAILED = -1;

    public static final int APPLY_FOR_GUIDE_SUCCESS = 0;
    public static final int APPLY_FOR_GUIDE_FAILED = -1;

    private OnLoginFinishListener mOnLoginFinishListener = null;
    private OnRegisterFinishListener mOnRegisterFinishListener = null;
    private OnGetVerifyCodeFinishListener mOnGetVerifyCodeFinishListener = null;
    private OnGetUserInfoFinishListener mOnGetUserInfoFinishListener = null;
    private OnApplyForGuideFinishListener mOnApplyForGuideFinishListener = null;

    private Context mContext;

    public UserHelper(Context context) {
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
                        SettingManager.getInstance().setUserPhoneNum(mobileNum);

                        GetUserInfoRequest userInfoRequest = new GetUserInfoRequest(response.userId);
                        GetUserInfoResponse userInfoResponse = InternetUtils.request(mContext, userInfoRequest);
                        if (userInfoResponse != null && userInfoResponse.userId > 0) {
                            SettingManager.getInstance().setUserName(userInfoResponse.userName);
                            SettingManager.getInstance().setUserGender(userInfoResponse.gender);
                            SettingManager.getInstance().setUserType(userInfoResponse.userType);
                            SettingManager.getInstance().setUserHeader(userInfoResponse.headUrl);
                        }

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
                        SettingManager.getInstance().setUserName(name);
                        SettingManager.getInstance().setUserGender(gender);
                        SettingManager.getInstance().setUserPhoneNum(mobileNum);
                        SettingManager.getInstance().setUserType(Tourist.USER_TYPE_TOURIST);

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
                    if (response != null && response.result == 0) {
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

    public static interface OnApplyForGuideFinishListener {
        public void onApplyForGuideFinish(int result);
    }

    public void applyForGuide(final String goodAtScenic, final long birthday, final int beGuideYear,
            final String guideCardUrl, final String guideCardId, final String location, final int city,
            OnApplyForGuideFinishListener listener) {
        mOnApplyForGuideFinishListener = listener;

        CustomThreadPool.asyncWork(new Runnable() {
            @Override
            public void run() {
                try {
                    ApplyForGuideRequest request = new ApplyForGuideRequest(goodAtScenic, birthday, beGuideYear,
                            guideCardUrl, guideCardId, location, city);
                    ApplyForGuideResponse response = InternetUtils.request(mContext, request);
                    if (response != null && response.result == 0) {

                        if (mOnApplyForGuideFinishListener != null) {
                            mOnApplyForGuideFinishListener.onApplyForGuideFinish(APPLY_FOR_GUIDE_SUCCESS);
                        }
                        return;
                    }
                } catch (NetWorkException e) {
                    e.printStackTrace();
                }

                if (mOnApplyForGuideFinishListener != null) {
                    mOnApplyForGuideFinishListener.onApplyForGuideFinish(APPLY_FOR_GUIDE_FAILED);
                }
            }
        });
    }

    public static interface OnGetUserInfoFinishListener {
        public void onGetUserInfoSuccess(Tourist tourist);

        public void onGetUserInfoFailed();
    }

    public void getUserInfo(final int userId, OnGetUserInfoFinishListener listener) {
        mOnGetUserInfoFinishListener = listener;

        CustomThreadPool.asyncWork(new Runnable() {
            @Override
            public void run() {
                try {
                    GetUserInfoRequest request = new GetUserInfoRequest(userId);
                    GetUserInfoResponse response = InternetUtils.request(mContext, request);
                    if (response != null && response.userId > 0) {
                        Tourist tourist = null;
                        if (response.userType == GetUserInfoResponse.TYPE_TOURGUIDE) {
                            tourist = new TourGuide(response.userId, response.userName, response.mobile,
                                    response.gender, response.userType, response.headUrl, response.goodAtScenic,
                                    response.birthday, response.beGuideYear, response.guideCardUrl,
                                    response.guideCardId, response.location, response.city);
                        } else {
                            tourist = new Tourist(response.userId, response.userName, response.mobile, response.gender,
                                    response.userType, response.headUrl);
                        }

                        if (mOnGetUserInfoFinishListener != null) {
                            mOnGetUserInfoFinishListener.onGetUserInfoSuccess(tourist);
                        }
                        return;
                    }
                } catch (NetWorkException e) {
                    e.printStackTrace();
                }

                if (mOnGetUserInfoFinishListener != null) {
                    mOnGetUserInfoFinishListener.onGetUserInfoFailed();
                }
            }
        });
    }

    public void destroy() {
        mContext = null;
        mOnLoginFinishListener = null;
        mOnRegisterFinishListener = null;
        mOnGetVerifyCodeFinishListener = null;
        mOnGetUserInfoFinishListener = null;
        mOnApplyForGuideFinishListener = null;
    }
}
