package com.find.guide.model;

import java.util.ArrayList;
import java.util.List;

import com.find.guide.api.guide.GetNearByGuideRequest;
import com.find.guide.api.guide.GetNearByGuideResponse;
import com.find.guide.api.guide.TourGuideView;
import com.plugin.common.utils.CustomThreadPool;
import com.plugin.internet.InternetUtils;
import com.plugin.internet.core.NetWorkException;

import android.content.Context;

public class GuideHelper {

    public static final int GET_NEARBY_GUIDE_SUCCESS = 0;
    public static final int GET_NEARBY_GUIDE_FAILED = -1;

    private OnGetNearByGuideListener mGetNearByGuideListener = null;
    private Context mContext;

    public GuideHelper(Context context) {
        mContext = context.getApplicationContext();
    }

    public interface OnGetNearByGuideListener {
        public void onGetNearByGuideFinish(int result, List<TourGuide> guides);
    }

    public void getNearByGuide(final String location, final double dist, final int start, final int rows,
            OnGetNearByGuideListener listener) {
        mGetNearByGuideListener = listener;

        CustomThreadPool.asyncWork(new Runnable() {
            @Override
            public void run() {
                try {
                    GetNearByGuideRequest request = new GetNearByGuideRequest(location, dist, start, rows);
                    GetNearByGuideResponse response = InternetUtils.request(mContext, request);
                    if (response != null) {
                        List<TourGuide> guides = new ArrayList<TourGuide>();
                        if (response.guides != null) {
                            guides = new ArrayList<TourGuide>();
                            for (TourGuideView guideView : response.guides) {
                                guides.add(new TourGuide(guideView.userId, guideView.userName, guideView.mobile,
                                        guideView.gender, guideView.userType, guideView.headUrl,
                                        guideView.goodAtScenic, guideView.birthday, guideView.beGuideYear,
                                        guideView.guideCardUrl, guideView.guideCardId, guideView.location,
                                        guideView.city));
                            }
                        }
                        if (mGetNearByGuideListener != null) {
                            mGetNearByGuideListener.onGetNearByGuideFinish(GET_NEARBY_GUIDE_SUCCESS, guides);
                        }
                        return;
                    }
                } catch (NetWorkException e) {
                    e.printStackTrace();
                }

                if (mGetNearByGuideListener != null) {
                    mGetNearByGuideListener.onGetNearByGuideFinish(GET_NEARBY_GUIDE_FAILED, null);
                }
            }
        });
    }

    public void destroy() {
        mContext = null;
        mGetNearByGuideListener = null;
    }
}
