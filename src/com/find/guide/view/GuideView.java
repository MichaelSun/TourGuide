package com.find.guide.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.map.OverlayItem;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.find.guide.R;
import com.find.guide.model.TourGuide;
import com.plugin.common.view.WebImageView;
import com.plugin.common.view.WebImageViewStatusListener;

public class GuideView extends LinearLayout implements WebImageViewStatusListener {

    private TextView mNameTv;

    private WebImageView mHeaderIv;

    private OverlayItem mOverlayItem;

    private OnUpdateGuideViewListener mOnUpdateGuideViewListener;

    public static interface OnUpdateGuideViewListener {
        public void onUpdate(OverlayItem item);
    }

    public GuideView(Context context) {
        this(context, null);
    }

    public GuideView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.map_guide_view_layout, this);
        setBackgroundResource(R.drawable.pop);
        setOrientation(LinearLayout.HORIZONTAL);

        mNameTv = (TextView) findViewById(R.id.guide_name);
        mHeaderIv = (WebImageView) findViewById(R.id.guide_header);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    public void setGuide(TourGuide guide, OnUpdateGuideViewListener listener) {
        mOnUpdateGuideViewListener = listener;
        if (guide != null) {
            mNameTv.setText(guide.getUserName());
            mHeaderIv.setWebImageViewStatusListener(this);
            mHeaderIv.setImageURI(new Uri.Builder().path(guide.getHeadUrl()).build());

            Bitmap bitmap = BMapUtil.getBitmapFromView(this);

            double[] lnglat = parseLocation(guide.getLocation());
            double lng = 39.915;
            double lat = 116.404;
            if (lnglat != null && lnglat.length == 2) {
                lng = lnglat[0];
                lat = lnglat[1];
            }
            GeoPoint pt = new GeoPoint((int) (lng * 1E6), (int) (lat * 1E6));
            mOverlayItem = new OverlayItem(pt, guide.getUserName(), "");
            mOverlayItem.setMarker(new BitmapDrawable(getResources(), bitmap));
        }
    }

    public OverlayItem getOverlayItem() {
        return mOverlayItem;
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

    @Override
    public void onPreLoadImage(ImageView view, String imageUrl) {

    }

    @Override
    public void onLoadImageSuccess(ImageView view, String imageUrl) {
        if (mOverlayItem != null) {
            Bitmap bitmap = BMapUtil.getBitmapFromView(this);
            mOverlayItem.setMarker(new BitmapDrawable(getResources(), bitmap));
            if (mOnUpdateGuideViewListener != null) {
                mOnUpdateGuideViewListener.onUpdate(mOverlayItem);
            }
        }
    }

    @Override
    public void onLoadImageFailed(ImageView view, String imageUrl) {

    }

}
