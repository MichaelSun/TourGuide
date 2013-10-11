package com.find.guide.app;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;
import com.find.guide.config.AppConfig;

public class TourGuideApplication extends Application {

	private static TourGuideApplication mInstance = null;

	public BMapManager mBMapManager = null;

	public static TourGuideApplication getInstance() {
		return mInstance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
		initBMapManager(this);
	}

	public void initBMapManager(Context context) {
		if (mBMapManager == null) {
			mBMapManager = new BMapManager(context);
		}

		if (!mBMapManager.init(AppConfig.BMAP_KEY, new MyGeneralListener())) {
			Toast.makeText(getInstance().getApplicationContext(),
					"BMapManager  初始化错误!", Toast.LENGTH_LONG).show();
		}
	}

	static class MyGeneralListener implements MKGeneralListener {

		@Override
		public void onGetNetworkState(int iError) {
			if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
				Toast.makeText(getInstance().getApplicationContext(),
						"您的网络出错啦！", Toast.LENGTH_LONG).show();
			} else if (iError == MKEvent.ERROR_NETWORK_DATA) {
				Toast.makeText(getInstance().getApplicationContext(),
						"输入正确的检索条件！", Toast.LENGTH_LONG).show();
			}
		}

		@Override
		public void onGetPermissionState(int iError) {
			if (iError == MKEvent.ERROR_PERMISSION_DENIED) {
				// 授权Key错误：
				Toast.makeText(getInstance().getApplicationContext(),
						"请输入正确的授权Key！",
						Toast.LENGTH_LONG).show();
			}
		}
	}
}
