package com.find.guide.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.find.guide.user.TourGuide;

public class GuideMapView extends MapView implements GuideView.OnUpdateGuideViewListener {

    private List<TourGuide> mGuides = null;

    private GuideOverlay mGuideOverlay = null;

    private OnGuideClickListener mOnGuideClickListener;

    public GuideMapView(Context context) {
        this(context, null);
    }

    public GuideMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mGuideOverlay = new GuideOverlay(null, this);
        getOverlays().add(mGuideOverlay);
    }

    @Override
    public void destroy() {
        if (mGuides != null)
            mGuides.clear();
        super.destroy();
    }

    public void updateGuideOverlay(List<TourGuide> guides) {
        mGuideOverlay.removeAll();
        mGuides = guides;
        if (guides != null && guides.size() > 0) {
            List<OverlayItem> items = new ArrayList<OverlayItem>();
            for (final TourGuide guide : guides) {
                GuideView view = new GuideView(getContext());
                view.setGuide(guide, this);
                items.add(view.getOverlayItem());
            }
            mGuideOverlay.addItem(items);
        }
        refresh();
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

    @Override
    public void onUpdate(OverlayItem item) {
        if (item != null) {
            mGuideOverlay.updateItem(item);
            refresh();
        }
    }

}
