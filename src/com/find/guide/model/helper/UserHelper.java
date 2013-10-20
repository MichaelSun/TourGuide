package com.find.guide.model.helper;

import java.util.List;

import com.find.guide.api.resource.UploadResourceRequest;
import com.find.guide.api.resource.UploadResourceResponse;
import com.find.guide.api.user.ApplyForGuideRequest;
import com.find.guide.api.user.ApplyForGuideResponse;
import com.find.guide.api.user.ChangeHeadRequest;
import com.find.guide.api.user.ChangeHeadResponse;
import com.find.guide.api.user.ChangeLocationRequest;
import com.find.guide.api.user.ChangeLocationResponse;
import com.find.guide.api.user.GetNearByGuideRequest;
import com.find.guide.api.user.GetNearByGuideResponse;
import com.find.guide.api.user.GetUserInfoRequest;
import com.find.guide.api.user.GetUserInfoResponse;
import com.find.guide.api.user.GetVerifyCodeRequest;
import com.find.guide.api.user.GetVerifyCodeResponse;
import com.find.guide.api.user.LoginRequest;
import com.find.guide.api.user.LoginResponse;
import com.find.guide.api.user.RegisterRequest;
import com.find.guide.api.user.RegisterResponse;
import com.find.guide.api.user.SearchGuideRequest;
import com.find.guide.api.user.SearchGuideResponse;
import com.find.guide.model.TourGuide;
import com.find.guide.model.Tourist;
import com.find.guide.setting.SettingManager;
import com.plugin.common.utils.CustomThreadPool;
import com.plugin.internet.InternetUtils;
import com.plugin.internet.core.NetWorkException;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.text.TextUtils;

public class UserHelper {

    public static final int SUCCESS = 0;
    public static final int FAILED = -1;
    public static final int NETWORK_ERROR = -2;

    private OnLoginFinishListener mOnLoginFinishListener = null;
    private OnRegisterFinishListener mOnRegisterFinishListener = null;
    private OnGetVerifyCodeFinishListener mOnGetVerifyCodeFinishListener = null;
    private OnGetUserInfoFinishListener mOnGetUserInfoFinishListener = null;
    private OnApplyForGuideFinishListener mOnApplyForGuideFinishListener = null;
    private OnGetNearByGuideListener mGetNearByGuideListener = null;
    private OnSearchGuideListener mOnSearchGuideListener = null;
    private OnChangeHeadListener mOnChangeHeadListener = null;
    private OnChangeLocationListener mOnChangeLocationListener = null;

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
                        SettingManager.getInstance().setUserPhoneNum(mobileNum);
                        SettingManager.getInstance().setUserName(response.userName);
                        SettingManager.getInstance().setUserGender(response.gender);
                        SettingManager.getInstance().setUserType(response.userType);
                        SettingManager.getInstance().setUserHeader(response.headUrl);
                        if (response.userPassport != null) {
                            SettingManager.getInstance().setTicket(response.userPassport.ticket);
                            SettingManager.getInstance().setSecretKey(response.userPassport.userSecretKey);
                        }
                        SettingManager.getInstance().setGuideMode(0);

                        if (mOnLoginFinishListener != null) {
                            mOnLoginFinishListener.onLoginFinish(SUCCESS);
                        }
                        return;
                    }
                } catch (NetWorkException e) {
                    e.printStackTrace();
                }

                if (mOnLoginFinishListener != null) {
                    mOnLoginFinishListener.onLoginFinish(NETWORK_ERROR);
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
                        SettingManager.getInstance().setGuideMode(0);

                        if (mOnRegisterFinishListener != null) {
                            mOnRegisterFinishListener.onRegisterFinish(SUCCESS);
                        }
                        return;
                    }
                } catch (NetWorkException e) {
                    e.printStackTrace();
                }

                if (mOnRegisterFinishListener != null) {
                    mOnRegisterFinishListener.onRegisterFinish(NETWORK_ERROR);
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
                    if (response != null) {
                        if (response.result == 0) {
                            if (mOnGetVerifyCodeFinishListener != null) {
                                mOnGetVerifyCodeFinishListener.onGetVerifyCodeFinish(SUCCESS);
                            }
                        } else {
                            if (mOnGetVerifyCodeFinishListener != null) {
                                mOnGetVerifyCodeFinishListener.onGetVerifyCodeFinish(FAILED);
                            }
                        }
                        return;
                    }
                } catch (NetWorkException e) {
                    e.printStackTrace();
                }

                if (mOnGetVerifyCodeFinishListener != null) {
                    mOnGetVerifyCodeFinishListener.onGetVerifyCodeFinish(NETWORK_ERROR);
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
                    String url = null;
                    if (!TextUtils.isEmpty(guideCardUrl)) {
                        UploadResourceRequest urRequest = new UploadResourceRequest(guideCardUrl, "jpg");
                        UploadResourceResponse urResponse = InternetUtils.request(mContext, urRequest);
                        if (urResponse != null) {
                            url = urResponse.url;
                        }
                    }

                    if (TextUtils.isEmpty(url)) {
                        if (mOnApplyForGuideFinishListener != null) {
                            mOnApplyForGuideFinishListener.onApplyForGuideFinish(FAILED);
                        }
                        return;
                    }

                    ApplyForGuideRequest request = new ApplyForGuideRequest(goodAtScenic, birthday, beGuideYear, url,
                            guideCardId, location, city);
                    ApplyForGuideResponse response = InternetUtils.request(mContext, request);
                    if (response != null && response.result == 0) {
                        if (response.result == 0) {
                            SettingManager.getInstance().setUserType(Tourist.USER_TYPE_TOURGUIDE);
                            if (mOnApplyForGuideFinishListener != null) {
                                mOnApplyForGuideFinishListener.onApplyForGuideFinish(SUCCESS);
                            }
                        } else {
                            if (mOnApplyForGuideFinishListener != null) {
                                mOnApplyForGuideFinishListener.onApplyForGuideFinish(FAILED);
                            }
                        }
                        return;
                    }
                } catch (NetWorkException e) {
                    e.printStackTrace();
                }

                if (mOnApplyForGuideFinishListener != null) {
                    mOnApplyForGuideFinishListener.onApplyForGuideFinish(NETWORK_ERROR);
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
                    if (response != null && response.tourist.getUserId() > 0) {
                        if (mOnGetUserInfoFinishListener != null) {
                            mOnGetUserInfoFinishListener.onGetUserInfoSuccess(response.tourist);
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

    public interface OnGetNearByGuideListener {
        public void onGetNearByGuideFinish(int result, List<TourGuide> guides);
    }

    public void getNearByGuide(final String location, final double dist, final int start, final int rows,
            OnGetNearByGuideListener listener) {
        mGetNearByGuideListener = listener;

        CustomThreadPool.asyncWork(new Runnable() {
            @Override
            public void run() {
                try {
                    GetNearByGuideRequest request = new GetNearByGuideRequest(location, dist, start, rows);
                    GetNearByGuideResponse response = InternetUtils.request(mContext, request);
                    if (response != null) {
                        List<TourGuide> guides = response.guides;
                        if (mGetNearByGuideListener != null) {
                            mGetNearByGuideListener.onGetNearByGuideFinish(SUCCESS, guides);
                        }
                        return;
                    }
                } catch (NetWorkException e) {
                    e.printStackTrace();
                }

                if (mGetNearByGuideListener != null) {
                    mGetNearByGuideListener.onGetNearByGuideFinish(NETWORK_ERROR, null);
                }
            }
        });
    }

    public static interface OnSearchGuideListener {
        public void onSearchGuide(int result, List<TourGuide> guides);
    }

    public void searchGuide(final int city, final int gender, final String scenic, final int start, final int rows,
            OnSearchGuideListener listener) {
        mOnSearchGuideListener = listener;
        CustomThreadPool.asyncWork(new Runnable() {
            @Override
            public void run() {
                try {
                    SearchGuideRequest request = new SearchGuideRequest(city, gender, scenic, start, rows);
                    SearchGuideResponse response = InternetUtils.request(mContext, request);
                    if (response != null) {
                        List<TourGuide> guides = response.guides;
                        if (mOnSearchGuideListener != null) {
                            mOnSearchGuideListener.onSearchGuide(SUCCESS, guides);
                        }
                        return;
                    }
                } catch (NetWorkException e) {
                    e.printStackTrace();
                }

                if (mOnSearchGuideListener != null) {
                    mOnSearchGuideListener.onSearchGuide(NETWORK_ERROR, null);
                }
            }
        });
    }

    public static interface OnChangeHeadListener {
        public void onChangeHead(int result);
    }

    public void changeHead(final String headPath, OnChangeHeadListener listener) {
        mOnChangeHeadListener = listener;

        CustomThreadPool.asyncWork(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = null;
                    if (!TextUtils.isEmpty(headPath)) {
                        UploadResourceRequest urRequest = new UploadResourceRequest(headPath, "jpg");
                        UploadResourceResponse urResponse = InternetUtils.request(mContext, urRequest);
                        if (urResponse != null) {
                            url = urResponse.url;
                        }
                    }

                    if (!TextUtils.isEmpty(url)) {
                        ChangeHeadRequest request = new ChangeHeadRequest(url);
                        ChangeHeadResponse response = InternetUtils.request(mContext, request);
                        if (response != null) {
                            if (response.result == 0) {
                                SettingManager.getInstance().setUserHeader(url);
                                if (mOnChangeHeadListener != null) {
                                    mOnChangeHeadListener.onChangeHead(SUCCESS);
                                }
                            } else {
                                if (mOnChangeHeadListener != null) {
                                    mOnChangeHeadListener.onChangeHead(FAILED);
                                }
                            }
                            return;
                        }
                    }
                } catch (NetWorkException e) {
                    e.printStackTrace();
                }
                if (mOnChangeHeadListener != null) {
                    mOnChangeHeadListener.onChangeHead(NETWORK_ERROR);
                }
            }
        });
    }

    public static interface OnChangeLocationListener {
        public void onChangeLocation(int result);
    }

    public void changeLocation(final String location, OnChangeLocationListener listener) {
        mOnChangeLocationListener = listener;

        CustomThreadPool.asyncWork(new Runnable() {
            @Override
            public void run() {
                try {
                    ChangeLocationRequest request = new ChangeLocationRequest(location);
                    ChangeLocationResponse response = InternetUtils.request(mContext, request);
                    if (response != null) {
                        if (response.result == 0) {
                            if (mOnChangeLocationListener != null) {
                                mOnChangeLocationListener.onChangeLocation(SUCCESS);
                            }
                        } else {
                            if (mOnChangeLocationListener != null) {
                                mOnChangeLocationListener.onChangeLocation(FAILED);
                            }
                        }
                        return;
                    }
                } catch (NetWorkException e) {
                    e.printStackTrace();
                }
                if (mOnChangeLocationListener != null) {
                    mOnChangeLocationListener.onChangeLocation(NETWORK_ERROR);
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
        mGetNearByGuideListener = null;
        mOnSearchGuideListener = null;
        mOnChangeHeadListener = null;
        mOnChangeLocationListener = null;
    }
}
