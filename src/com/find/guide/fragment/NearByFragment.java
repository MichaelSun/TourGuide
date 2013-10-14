package com.find.guide.fragment;

import java.util.List;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.MyLocationOverlay.LocationMode;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.find.guide.R;
import com.find.guide.activity.BookingActivity;
import com.find.guide.app.TourGuideApplication;
import com.find.guide.model.GuideHelper;
import com.find.guide.model.GuideHelper.OnGetNearByGuideListener;
import com.find.guide.model.TourGuide;
import com.find.guide.view.GuideMapView;
import com.find.guide.view.GuideMapView.OnGuideClickListener;

public class NearByFragment extends Fragment {

    private GuideMapView mMapView = null;
    private MapController mMapController = null;

    LocationClient mLocClient;
    LocationData locData = null;
    MyLocationListenner myListener = new MyLocationListenner();
    MyLocationOverlay myLocationOverlay = null;

    private boolean mIsFirstLocation = true;

    private GuideHelper mGuideHelper = null;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mGuideHelper = new GuideHelper(TourGuideApplication.getInstance());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, null);

        mMapView = (GuideMapView) view.findViewById(R.id.map_view);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initMap();
        initLocation();
        initLocationOverlay();

        mMapView.setOnGuideClickListener(new OnGuideClickListener() {
            @Override
            public void onGuideClick(TourGuide guide) {
                bookingGuide(guide);
            }
        });

    }

    private void initMap() {
        mMapView.setBuiltInZoomControls(true);
        mMapController = mMapView.getController();
        mMapController.enableClick(true);
        mMapController.setZoom(13);
        mMapView.setBuiltInZoomControls(true);

        GeoPoint p = new GeoPoint((int) (39.933859 * 1E6), (int) (116.400191 * 1E6));
        mMapController.setCenter(p);
    }

    private void initLocation() {
        mLocClient = new LocationClient(getActivity());
        locData = new LocationData();
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();
    }

    private void initLocationOverlay() {
        // 定位图层初始化
        myLocationOverlay = new MyLocationOverlay(mMapView);
        // 设置定位数据
        myLocationOverlay.setData(locData);
        // 添加定位图层
        mMapView.getOverlays().add(myLocationOverlay);
        myLocationOverlay.enableCompass();
        // 修改定位数据后刷新图层生效
        mMapView.refresh();
    }

    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null)
                return;

            locData.latitude = location.getLatitude();
            locData.longitude = location.getLongitude();
            // 如果不显示定位精度圈，将accuracy赋值为0即可
            locData.accuracy = location.getRadius();
            locData.direction = location.getDerect();
            // 更新定位数据
            myLocationOverlay.setData(locData);
            // 更新图层数据执行刷新后生效
            mMapView.refresh();

            if (mIsFirstLocation) {
                // 移动地图到定位点
                Log.d("LocationOverlay", "receive location, animate to it");
                mMapController.animateTo(new GeoPoint((int) (locData.latitude * 1e6), (int) (locData.longitude * 1e6)));
                myLocationOverlay.setLocationMode(LocationMode.NORMAL);

                getNearByGuide(locData.latitude + "," + locData.longitude);
            }
            mIsFirstLocation = false;
        }

        public void onReceivePoi(BDLocation poiLocation) {
            if (poiLocation == null) {
                return;
            }
        }
    }

    private void getNearByGuide(String location) {
        mGuideHelper.getNearByGuide(location, 10, 0, 50, mOnGetNearByGuideListener);
    }

    private OnGetNearByGuideListener mOnGetNearByGuideListener = new OnGetNearByGuideListener() {

        @Override
        public void onGetNearByGuideFinish(int result, final List<TourGuide> guides) {
            if (result == GuideHelper.GET_NEARBY_GUIDE_SUCCESS) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mMapView.updateGuideOverlay(guides);
                    }
                });
            }
        }
    };

    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (mGuideHelper != null) {
            mGuideHelper.destroy();
            mGuideHelper = null;
        }
        if (mLocClient != null) {
            mLocClient.stop();
        }
        mMapView.destroy();
        mHandler.removeCallbacksAndMessages(null);

        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.location, menu);
    }

    private void bookingGuide(TourGuide guide) {
        Intent intent = new Intent(TourGuideApplication.getInstance(), BookingActivity.class);
        // TODO
        startActivity(intent);
    }

}
