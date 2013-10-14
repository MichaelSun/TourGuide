package com.find.guide.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.find.guide.model.TourGuide;

public class GuideMapView extends MapView {

    private GuideOverlay mOverlay = null;

    private List<TourGuide> mGuides = null;

    private OnGuideClickListener mOnGuideClickListener;

    public GuideMapView(Context context) {
        this(context, null);
    }

    public GuideMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void updateGuideOverlay(List<TourGuide> guides) {
        if (mOverlay == null) {
            mOverlay = new GuideOverlay(null, this);
            getOverlays().add(mOverlay);
        }

        mGuides = guides;
        mOverlay.removeAll();
        
        if (guides != null && guides.size() > 0) {
            List<OverlayItem> items = new ArrayList<OverlayItem>();
            for (TourGuide guide : guides) {
                GuideView view = new GuideView(getContext());
                view.setGuide(guide);
                Bitmap bitmap = BMapUtil.getBitmapFromView(view);

                double[] lnglat = parseLocation(guide.location);
                double lng = 39.915;
                double lat = 116.404;
                if (lnglat != null && lnglat.length == 2) {
                    lng = lnglat[0];
                    lat = lnglat[1];
                }
                GeoPoint pt = new GeoPoint((int) (lng * 1E6), (int) (lat * 1E6));
                OverlayItem item = new OverlayItem(pt, "导游1", "");
                item.setMarker(new BitmapDrawable(getResources(), bitmap));
                items.add(item);
            }
            mOverlay.addItem(items);
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
//            OverlayItem item = getItem(index);
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
