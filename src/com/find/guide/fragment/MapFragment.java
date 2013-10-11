package com.find.guide.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.find.guide.R;

public class MapFragment extends Fragment {

    private MapView mMapView = null;

    private MapController mMapController = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, null);
        mMapView = (MapView) view.findViewById(R.id.map_view);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mMapView.setBuiltInZoomControls(true);
        // 设置启用内置的缩放控件
        mMapController = mMapView.getController();
        // 得到mMapView的控制权,可以用它控制和驱动平移和缩放
        GeoPoint point = new GeoPoint((int) (39.915 * 1E6), (int) (116.404 * 1E6));
        // 用给定的经纬度构造一个GeoPoint，单位是微度 (度 * 1E6)
        mMapController.setCenter(point);// 设置地图中心点
        mMapController.setZoom(12);// 设置地图zoom级别
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
        mMapView.destroy();
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.location, menu);
    }

}
