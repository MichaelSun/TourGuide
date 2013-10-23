package com.find.guide.app;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;
import com.find.guide.R;
import com.find.guide.config.AppConfig;
import com.find.guide.config.AppRuntime;
import com.find.guide.model.helper.CityManager;
import com.find.guide.setting.SettingManager;
import com.find.guide.utils.Toasts;
import com.find.guide.utils.XMLTables;
import com.plugin.common.utils.UtilsConfig;
import com.plugin.common.utils.UtilsRuntime;
import com.plugin.internet.InternetUtils;
import com.plugin.internet.core.HttpConnectHookListener;
import com.plugin.internet.core.NetWorkException;
import com.plugin.internet.core.RequestBase;

public class TourGuideApplication extends Application {

    public static final int CODE_TICKET_INVALID = 7;

    private static final int SHOW_SERVER_CODE_TIPS = 1;
    private static final int SHOW_LOCAL_NETWORK_ERROR = 2;
    private static final int SHOW_NETWORK_ERROR = 3;

    private static TourGuideApplication mInstance = null;

    public BMapManager mBMapManager = null;

    public static TourGuideApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        String curProcessName = UtilsRuntime.getCurProcessName(this);
        if (!TextUtils.isEmpty(curProcessName) && curProcessName.equals(getPackageName())) {
            SettingManager.getInstance().init(this);
            UtilsConfig.init(this);
            AppRuntime.APP_DEVICE_INFO = new AppRuntime.AppDeviceInfo(this);

            AppRuntime.gXMLTables = new XMLTables();
            AppRuntime.gXMLTables.loadXML(getResources().getXml(R.xml.error_tips));
            CityManager.getInstance().loadXML(getResources().getXml(R.xml.city));

            setHttpHookListener();

            initBMapManager(this);
        }
    }

    public void initBMapManager(Context context) {
        if (mBMapManager == null) {
            mBMapManager = new BMapManager(context);
        }

        if (!mBMapManager.init(AppConfig.BMAP_KEY, new MyGeneralListener())) {
            Toast.makeText(getInstance().getApplicationContext(), "BMapManager  初始化错误!", Toast.LENGTH_LONG).show();
        }
    }

    static class MyGeneralListener implements MKGeneralListener {

        @Override
        public void onGetNetworkState(int iError) {
            if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
                Toasts.getInstance(getInstance()).show("网络错误", Toast.LENGTH_SHORT);
            } else if (iError == MKEvent.ERROR_NETWORK_DATA) {
                Toasts.getInstance(getInstance()).show("网络错误", Toast.LENGTH_SHORT);
            }
        }

        @Override
        public void onGetPermissionState(int iError) {
            if (iError == MKEvent.ERROR_PERMISSION_DENIED) {
                // 授权Key错误：
                Toasts.getInstance(getInstance()).show("无效的地图授权Key", Toast.LENGTH_SHORT);
            }
        }
    }

    private void setHttpHookListener() {
        InternetUtils.setHttpHookListener(getApplicationContext(), new HttpConnectHookListener() {

            @Override
            public void onPreHttpConnect(String baseUrl, String method, Bundle requestParams) {
            }

            @Override
            public void onPostHttpConnect(String result, int httpStatus) {
            }

            @Override
            public void onHttpConnectError(int code, String data, Object obj) {
                if (code > 0) {
                    // server error code
                    Message msg = new Message();
                    msg.what = SHOW_SERVER_CODE_TIPS;
                    msg.arg1 = code;
                    mHandler.sendMessage(msg);
                } else if (code == NetWorkException.NETWORK_NOT_AVILABLE) {
                    Message msg = new Message();
                    msg.what = SHOW_LOCAL_NETWORK_ERROR;
                    mHandler.sendMessage(msg);
                } else {
                    Message msg = new Message();
                    msg.what = SHOW_NETWORK_ERROR;
                    mHandler.sendMessage(msg);
                }
            }
        });
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            RequestBase request = (RequestBase) msg.obj;

            switch (msg.what) {
            case SHOW_SERVER_CODE_TIPS:
                // if (msg.arg1 == CODE_TICKET_INVALID) {
                // AppRuntime.logout();
                // // 跳到主页
                //
                // }
                if (request == null || !request.canIgnoreResult()) {
                    Toasts.getInstance(getApplicationContext())
                            .show(AppRuntime.gXMLTables.getProperty(AppConfig.ROOT_CATEGORY, AppConfig.SERVER_CODE,
                                    msg.arg1), Toast.LENGTH_SHORT);
                }
                break;
            case SHOW_LOCAL_NETWORK_ERROR:
                if (request == null || !request.canIgnoreResult()) {
                    Toasts.getInstance(getApplicationContext()).show(
                            AppRuntime.gXMLTables.getDefaultForCategory(AppConfig.ROOT_CATEGORY), Toast.LENGTH_SHORT);
                }
                break;
            case SHOW_NETWORK_ERROR:
                if (request == null || !request.canIgnoreResult()) {
                    Toasts.getInstance(getApplicationContext()).show(
                            AppRuntime.gXMLTables.getProperty(AppConfig.ROOT_CATEGORY, "internet_error", 0),
                            Toast.LENGTH_SHORT);
                }
                break;
            }
        }
    };
}
