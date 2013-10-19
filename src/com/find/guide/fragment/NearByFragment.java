package com.find.guide.fragment;

import java.util.ArrayList;
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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.find.guide.activity.BroadcastActivity;
import com.find.guide.adapter.GuideAdapter;
import com.find.guide.app.TourGuideApplication;
import com.find.guide.config.AppRuntime;
import com.find.guide.model.helper.UserHelper;
import com.find.guide.model.helper.UserHelper.OnGetNearByGuideListener;
import com.find.guide.model.TourGuide;
import com.find.guide.utils.Toasts;
import com.find.guide.view.GuideMapView;
import com.find.guide.view.GuideMapView.OnGuideClickListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

public class NearByFragment extends Fragment implements View.OnClickListener {

    private View mMapLocationView;
    private View mBroadcastView;

    private View mMapHintView;
    private ProgressBar mMapHintPb;
    private TextView mMapHintTv;

    private GuideMapView mMapView = null;
    private MapController mMapController = null;

    private PullToRefreshListView mListView;
    private GuideAdapter mGuideAdapter;

    private List<TourGuide> mTourGuides = new ArrayList<TourGuide>();

    LocationClient mLocClient;
    LocationData locData = null;
    MyLocationListenner myListener = new MyLocationListenner();
    MyLocationOverlay myLocationOverlay = null;

    private boolean mIsFirstLocation = true;
    private boolean mIsRequestLocation = false;

    private UserHelper mUserHelper = null;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    static enum ShowMode {
        MAP, LIST
    }

    private ShowMode mShowMode = ShowMode.MAP;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mUserHelper = new UserHelper(TourGuideApplication.getInstance());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nearby, null);

        mMapView = (GuideMapView) view.findViewById(R.id.map_view);

        mMapLocationView = view.findViewById(R.id.map_refresh_btn);
        mMapLocationView.setOnClickListener(this);
        mBroadcastView = view.findViewById(R.id.broadcast_btn);
        mBroadcastView.setOnClickListener(this);
        mMapHintView = view.findViewById(R.id.map_hint_layout);
        mMapHintPb = (ProgressBar) view.findViewById(R.id.map_hint_pb);
        mMapHintTv = (TextView) view.findViewById(R.id.map_hint_tv);
        mMapHintView.setVisibility(View.GONE);

        mListView = (PullToRefreshListView) view.findViewById(R.id.listview);
        mListView.setVisibility(View.GONE);
        mListView.setShowIndicator(false);
        mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                requestLocation();
            }
        });
        mGuideAdapter = new GuideAdapter(getActivity(), mTourGuides);

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.map_refresh_btn:
            requestLocation();
            break;
        case R.id.broadcast_btn:
            sendBroadcast();
            break;
        }
    }

    private void requestLocation() {
        if (!mIsRequestLocation) {
            mIsRequestLocation = true;
            mLocClient.requestLocation();
            if (mShowMode == ShowMode.MAP) {
                mMapHintView.setVisibility(View.VISIBLE);
                mMapHintPb.setVisibility(View.VISIBLE);
                mMapHintTv.setVisibility(View.VISIBLE);
                mMapHintTv.setText(R.string.loading_location);
            }
        }
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

            AppRuntime.gLocation = locData.latitude + "," + locData.longitude;

            if (mIsFirstLocation || mIsRequestLocation) {
                if (location.getLatitude() <= 0.000001 && location.getLongitude() < 0.000001) {
                    mIsRequestLocation = false;
                    mListView.onRefreshComplete();
                    Toasts.getInstance(getActivity()).show(R.string.location_failed, Toast.LENGTH_SHORT);
                    if (mShowMode == ShowMode.MAP) {
                        mMapHintView.setVisibility(View.GONE);
                        mMapHintPb.setVisibility(View.GONE);
                        mMapHintTv.setVisibility(View.GONE);
                        mMapHintTv.setText("");
                    }
                } else {
                    // 移动地图到定位点
                    Log.d("LocationOverlay", "receive location, animate to it");
                    mMapController.animateTo(new GeoPoint((int) (locData.latitude * 1e6),
                            (int) (locData.longitude * 1e6)));
                    mIsRequestLocation = false;
                    myLocationOverlay.setLocationMode(LocationMode.NORMAL);

                    getNearByGuide(locData.latitude + "," + locData.longitude);
                }
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
        if (mShowMode == ShowMode.MAP) {
            mMapHintView.setVisibility(View.VISIBLE);
            mMapHintPb.setVisibility(View.VISIBLE);
            mMapHintTv.setVisibility(View.VISIBLE);
            mMapHintTv.setText(R.string.loading_nearby_guide);
        }

        mUserHelper.getNearByGuide(location, 500, 0, 100, mOnGetNearByGuideListener);
    }

    private OnGetNearByGuideListener mOnGetNearByGuideListener = new OnGetNearByGuideListener() {

        @Override
        public void onGetNearByGuideFinish(final int result, final List<TourGuide> guides) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mListView.onRefreshComplete();
                    if (result == UserHelper.SUCCESS) {
                        mTourGuides.clear();
                        if (guides != null) {
                            mTourGuides.addAll(guides);
                        }
                        mGuideAdapter.notifyDataSetChanged();
                        mMapView.updateGuideOverlay(guides);
                    }
                    if (mShowMode == ShowMode.MAP) {
                        mMapHintView.setVisibility(View.GONE);
                        mMapHintPb.setVisibility(View.GONE);
                        mMapHintTv.setVisibility(View.GONE);
                        mMapHintTv.setText("");
                    }
                }
            });
        }
    };

    private void sendBroadcast() {
        Intent intent = new Intent(getActivity(), BroadcastActivity.class);
        startActivity(intent);
    }

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
        if (mUserHelper != null) {
            mUserHelper.destroy();
            mUserHelper = null;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_show_map && mShowMode == ShowMode.LIST) {
            mShowMode = ShowMode.MAP;
            mListView.setVisibility(View.GONE);
            return true;
        } else if (item.getItemId() == R.id.menu_show_list && mShowMode == ShowMode.MAP) {
            mShowMode = ShowMode.LIST;
            mListView.setVisibility(View.VISIBLE);
            if (mListView.getRefreshableView().getAdapter() == null) {
                mListView.setAdapter(mGuideAdapter);
            } else {
                mGuideAdapter.notifyDataSetChanged();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void bookingGuide(TourGuide guide) {
        Intent intent = new Intent(TourGuideApplication.getInstance(), BookingActivity.class);
        intent.putExtra(BookingActivity.INTENT_EXTRA_GUIDE, guide);
        startActivity(intent);
    }

}
