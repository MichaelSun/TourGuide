package com.find.guide.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import com.find.guide.setting.SettingManager;
import com.find.guide.user.TourGuide;
import com.find.guide.user.Tourist;
import com.find.guide.user.UserHelper;
import com.find.guide.user.UserHelper.OnGetNearByGuideListener;
import com.find.guide.view.GuideMapView;
import com.find.guide.view.GuideMapView.OnGuideClickListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class NearByFragment extends Fragment {

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

    private int ROWS = 51;
    private double DIST = 50;

    private boolean mIsGettingNearBy = false;

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

        mMapHintView = view.findViewById(R.id.map_hint_layout);
        mMapHintPb = (ProgressBar) view.findViewById(R.id.map_hint_pb);
        mMapHintTv = (TextView) view.findViewById(R.id.map_hint_tv);
        mMapHintView.setVisibility(View.GONE);

        mListView = (PullToRefreshListView) view.findViewById(R.id.listview);
        mListView.setVisibility(View.GONE);
        mListView.setShowIndicator(false);
        mListView.setMode(Mode.PULL_FROM_START);
        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                requestLocation();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                loadMoreGuide();
            }

        });

        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int pos = position - mListView.getRefreshableView().getHeaderViewsCount();
                if (pos >= 0 && pos < mTourGuides.size()) {
                    TourGuide guide = mTourGuides.get(pos);
                    Intent intent = new Intent(getActivity(), BookingActivity.class);
                    intent.putExtra(BookingActivity.INTENT_EXTRA_GUIDE, guide);
                    startActivity(intent);
                }
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

        GeoPoint p = null;
        String location = SettingManager.getInstance().getLastLocation();
        if (!TextUtils.isEmpty(location)) {
            double[] latlng = parseLocation(location);
            if (latlng != null && latlng.length >= 2) {
                p = new GeoPoint((int) (latlng[0] * 1E6), (int) (latlng[1] * 1E6));
            }
        }
        if (p == null)
            p = new GeoPoint((int) (39.933859 * 1E6), (int) (116.400191 * 1E6));
        mMapController.setCenter(p);
    }

    private double[] parseLocation(String location) {
        double[] lnglat = new double[2];
        if (!TextUtils.isEmpty(location)) {
            String[] s = location.split(",");
            if (s != null && s.length == 2) {
                try {
                    lnglat[0] = Double.parseDouble(s[0]);
                    lnglat[1] = Double.parseDouble(s[1]);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        return lnglat;
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

        mMapHintView.setVisibility(View.VISIBLE);
        mMapHintPb.setVisibility(View.VISIBLE);
        mMapHintTv.setVisibility(View.VISIBLE);
        mMapHintTv.setText(R.string.loading_location);
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

    private void requestLocation() {
        if (!mIsRequestLocation) {
            mIsRequestLocation = true;
            mLocClient.requestLocation();

            mMapHintView.setVisibility(View.VISIBLE);
            mMapHintPb.setVisibility(View.VISIBLE);
            mMapHintTv.setVisibility(View.VISIBLE);
            mMapHintTv.setText(R.string.loading_location);
        }
    }

    private void loadMoreGuide() {
        if (mIsGettingNearBy) {
            mListView.onRefreshComplete();
            return;
        }

        String location = AppRuntime.gLocation;
        int start = 0;
        if (mTourGuides != null) {
            start = mTourGuides.size();
        }
        mIsGettingNearBy = true;
        mUserHelper.getNearByGuide(location, DIST, start, ROWS, mOnGetNearByGuideListener);
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
            SettingManager.getInstance().setLastLocation(AppRuntime.gLocation);

            if (mIsFirstLocation || mIsRequestLocation) {
                // 移动地图到定位点
                Log.d("LocationOverlay", "receive location, animate to it");
                mMapController.animateTo(new GeoPoint((int) (locData.latitude * 1e6), (int) (locData.longitude * 1e6)));
                mIsRequestLocation = false;
                myLocationOverlay.setLocationMode(LocationMode.NORMAL);

                String sloc = locData.latitude + "," + locData.longitude;
                getNearByGuide(sloc);

                if (SettingManager.getInstance().getUserId() > 0
                        && SettingManager.getInstance().getUserType() == Tourist.USER_TYPE_TOURGUIDE) {
                    mUserHelper.changeLocation(sloc, null);
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
        mMapHintView.setVisibility(View.VISIBLE);
        mMapHintPb.setVisibility(View.VISIBLE);
        mMapHintTv.setVisibility(View.VISIBLE);
        mMapHintTv.setText(R.string.loading_nearby_guide);

        mIsGettingNearBy = true;
        mUserHelper.getNearByGuide(location, DIST, 0, ROWS, mOnGetNearByGuideListener);
    }

    private OnGetNearByGuideListener mOnGetNearByGuideListener = new OnGetNearByGuideListener() {

        @Override
        public void onGetNearByGuideFinish(final int result, final List<TourGuide> guides) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mIsGettingNearBy = false;
                    mListView.onRefreshComplete();
                    if (result == UserHelper.SUCCESS) {
                        if (guides == null || guides.size() == 0) {
                            mListView.setMode(Mode.PULL_FROM_START);
                        } else {
                            if (guides.size() >= ROWS) {
                                mListView.setMode(Mode.BOTH);
                                guides.remove(guides.size() - 1);
                            } else {
                                mListView.setMode(Mode.PULL_FROM_START);
                            }
                            mTourGuides.clear();
                            mTourGuides.addAll(guides);
                            mGuideAdapter.notifyDataSetChanged();
                            mMapView.updateGuideOverlay(guides);
                        }
                    }

                    mMapHintView.setVisibility(View.GONE);
                    mMapHintPb.setVisibility(View.GONE);
                    mMapHintTv.setVisibility(View.GONE);
                    mMapHintTv.setText("");
                }
            });
        }

        @Override
        public void onGetMoreNearByGuideFinish(final int result, final List<TourGuide> guides) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mIsGettingNearBy = false;
                    mListView.onRefreshComplete();
                    if (result == UserHelper.SUCCESS) {
                        if (guides == null || guides.size() == 0) {
                            mListView.setMode(Mode.PULL_FROM_START);
                        } else {
                            if (guides.size() >= ROWS) {
                                mListView.setMode(Mode.BOTH);
                                guides.remove(guides.size() - 1);
                            } else {
                                mListView.setMode(Mode.PULL_FROM_START);
                            }
                            mTourGuides.addAll(guides);
                            mGuideAdapter.notifyDataSetChanged();
                            mMapView.updateGuideOverlay(mTourGuides);
                        }
                    }
                }
            });
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
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem item = menu.findItem(R.id.menu_show_mode);
        if (mShowMode == ShowMode.MAP) {
            mListView.setVisibility(View.GONE);
            item.setTitle(R.string.menu_show_list);
            item.setIcon(R.drawable.icon_action_list);
        } else {
            mListView.setVisibility(View.VISIBLE);
            if (mListView.getRefreshableView().getAdapter() == null) {
                mListView.setAdapter(mGuideAdapter);
            } else {
                mGuideAdapter.notifyDataSetChanged();
            }
            item.setTitle(R.string.menu_show_map);
            item.setIcon(R.drawable.icon_action_map);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_show_mode) {
            if (mShowMode == ShowMode.LIST) {
                mShowMode = ShowMode.MAP;
            } else {
                mShowMode = ShowMode.LIST;
            }
            getActivity().invalidateOptionsMenu();
        } else if (item.getItemId() == R.id.menu_broadcast) {
            sendBroadcast();
        } else if (item.getItemId() == R.id.menu_refresh) {
            if (mShowMode == ShowMode.MAP) {
                requestLocation();
            } else {
                mListView.setRefreshing(true);
            }
        }
        return true;
    }

    private void sendBroadcast() {
        Intent intent = new Intent(getActivity(), BroadcastActivity.class);
        startActivity(intent);
    }

    private void bookingGuide(TourGuide guide) {
        Intent intent = new Intent(TourGuideApplication.getInstance(), BookingActivity.class);
        intent.putExtra(BookingActivity.INTENT_EXTRA_GUIDE, guide);
        startActivity(intent);
    }

}
