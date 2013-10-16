package com.find.guide.fragment;

import java.util.ArrayList;
import java.util.List;

import com.find.guide.R;
import com.find.guide.app.TourGuideApplication;
import com.find.guide.model.GuideEvent;
import com.find.guide.model.InviteEvent;
import com.find.guide.model.Tourist;
import com.find.guide.model.helper.GuideHelper;
import com.find.guide.model.helper.InviteHelper;
import com.find.guide.model.helper.InviteHelper.OnGetHistoricalInviteEventsListener;
import com.find.guide.setting.SettingManager;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

public class ProfileRecordFragment extends Fragment {

    private ListView mListView;

    private InviteHelper mInviteHelper = null;
    private GuideHelper mGuideHelper = null;

    private SettingManager mSettingManager;

    private BaseAdapter mRecordAdapter;
    private List<InviteEvent> mInviteEvents = new ArrayList<InviteEvent>();
    private List<GuideEvent> mGuideEvent = new ArrayList<GuideEvent>();

    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mInviteHelper = new InviteHelper(TourGuideApplication.getInstance());
        mGuideHelper = new GuideHelper(TourGuideApplication.getInstance());

        mSettingManager = SettingManager.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_record, null);

        mListView = (ListView) view.findViewById(R.id.listview);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mListView.addHeaderView(LayoutInflater.from(getActivity()).inflate(R.layout.header_view_layout, null));
        mListView.addFooterView(LayoutInflater.from(getActivity()).inflate(R.layout.header_view_layout, null));

        if (mSettingManager.getUserId() > 0) {
            if (mSettingManager.getUserType() == Tourist.USER_TYPE_TOURGUIDE) {
                mRecordAdapter = new GuideRecordAdapter(mGuideEvent);
            } else {
                mRecordAdapter = new InviteRecordAdapter(mInviteEvents);
            }
            mListView.setAdapter(mRecordAdapter);

            mInviteHelper.getHistoricalInviteEvents(0, 100, mGetHistoricalInviteEventsListener);
        }
    }

    private OnGetHistoricalInviteEventsListener mGetHistoricalInviteEventsListener = new OnGetHistoricalInviteEventsListener() {
        @Override
        public void onGetHistoricalInviteEvents(int result, final List<InviteEvent> inviteEvents) {
            if (result == InviteHelper.SUCCESS) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mInviteEvents.clear();
                        if (inviteEvents != null) {
                            mInviteEvents.addAll(inviteEvents);
                        }
                        mRecordAdapter.notifyDataSetChanged();
                    }
                });
            }
        }
    };

    @Override
    public void onDestroy() {
        if (mInviteHelper != null) {
            mInviteHelper.destroy();
            mInviteHelper = null;
        }
        if (mGuideHelper != null) {
            mGuideHelper.destroy();
            mGuideHelper = null;
        }
        super.onDestroy();
    }

}
