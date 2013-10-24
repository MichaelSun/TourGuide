package com.find.guide.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.find.guide.model.TourGuide;

public class GuideMapView extends MapView {

    private List<TourGuide> mGuides = null;

    private List<View> mViewList = new ArrayList<View>();

    private OnGuideClickListener mOnGuideClickListener;

    public GuideMapView(Context context) {
        this(context, null);
    }

    public GuideMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    @Override
    public void destroy() {
        if (mGuides != null)
            mGuides.clear();
        if (mViewList != null) {
            mViewList.clear();
        }
        super.destroy();
    }

    public void updateGuideOverlay(List<TourGuide> guides) {
        for (View view : mViewList) {
            removeView(view);
        }
        mViewList.clear();
        if (mGuides != null)
            mGuides.clear();
        mGuides = guides;

        if (guides != null && guides.size() > 0) {
            // guides.add(new TourGuide(10001, "小宇", "1212", 1, 1, "", "",
            // 1029312, 1231, "", "12312", "39.958981,116.434364",
            // 200001, 1, 1));
            // List<OverlayItem> items = new ArrayList<OverlayItem>();
            for (final TourGuide guide : guides) {
                GuideView view = new GuideView(getContext());
                view.setGuide(guide);
                // Bitmap bitmap = BMapUtil.getBitmapFromView(view);
                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnGuideClickListener != null)
                            mOnGuideClickListener.onGuideClick(guide);
                    }
                });
                mViewList.add(view);

                double[] lnglat = parseLocation(guide.getLocation());
                double lng = 39.915;
                double lat = 116.404;
                if (lnglat != null && lnglat.length == 2) {
                    lng = lnglat[0];
                    lat = lnglat[1];
                }
                GeoPoint pt = new GeoPoint((int) (lng * 1E6), (int) (lat * 1E6));
                // pt = CoordinateConvert.fromGcjToBaidu(pt);
                // pt = CoordinateConvert.fromWgs84ToBaidu(pt);
                // OverlayItem item = new OverlayItem(pt, guide.getUserName(),
                // "");
                // item.setMarker(new BitmapDrawable(getResources(), bitmap));
                // items.add(item);

                this.addView(view, new MapView.LayoutParams(MapView.LayoutParams.WRAP_CONTENT,
                        MapView.LayoutParams.WRAP_CONTENT, pt, MapView.LayoutParams.TOP));
            }
        }

        refresh();
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

    public static interface OnGuideClickListener {
        public void onGuideClick(TourGuide guide);
    }

    public void setOnGuideClickListener(OnGuideClickListener listener) {
        mOnGuideClickListener = listener;
    }

    class GuideOverlay extends ItemizedOverlay<OverlayItem> {

        public GuideOverlay(Drawable defaultMarker, MapView mapView) {
            super(defaultMarker, mapView);
        }

        @Override
        public boolean onTap(int index) {
            // OverlayItem item = getItem(index);
            if (mGuides != null && mGuides.size() > index) {
                TourGuide guide = mGuides.get(index);
                if (mOnGuideClickListener != null) {
                    mOnGuideClickListener.onGuideClick(guide);
                }
            }
            return true;
        }

        @Override
        public boolean onTap(GeoPoint pt, MapView mMapView) {
            return false;
        }

    }

}
