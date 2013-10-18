package com.find.guide.fragment;

import java.util.ArrayList;
import java.util.List;

import com.find.guide.R;
import com.find.guide.activity.BaseActivity;
import com.find.guide.adapter.GuideRecordAdapter;
import com.find.guide.adapter.InviteRecordAdapter;
import com.find.guide.app.TourGuideApplication;
import com.find.guide.model.GuideEvent;
import com.find.guide.model.InviteEvent;
import com.find.guide.model.Tourist;
import com.find.guide.model.helper.GuideHelper;
import com.find.guide.model.helper.GuideHelper.OnGetHistoricalGuideEventsListener;
import com.find.guide.model.helper.InviteHelper;
import com.find.guide.model.helper.InviteHelper.OnGetHistoricalInviteEventsListener;
import com.find.guide.setting.SettingManager;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

public class ProfileRecordFragment extends Fragment {

    private PullToRefreshListView mListView;

    private InviteHelper mInviteHelper = null;
    private GuideHelper mGuideHelper = null;

    private SettingManager mSettingManager;

    private BaseAdapter mRecordAdapter;
    private List<InviteEvent> mInviteEvents = new ArrayList<InviteEvent>();
    private List<GuideEvent> mGuideEvents = new ArrayList<GuideEvent>();

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

        mListView = (PullToRefreshListView) view.findViewById(R.id.listview);
        mListView.setShowIndicator(false);
        mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                if (mSettingManager.getUserId() > 0) {
                    refresh();
                } else {
                    mListView.onRefreshComplete();
                }
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (mSettingManager.getUserId() <= 0) {
                ((BaseActivity) getActivity()).getActionBar().setTitle(R.string.profile_record);
                if (mRecordAdapter != null) {
                    mInviteEvents.clear();
                    mGuideEvents.clear();
                    mRecordAdapter.notifyDataSetChanged();
                }
            } else if (mSettingManager.getUserType() == Tourist.USER_TYPE_TOURGUIDE) {
                String title = getString(R.string.profile_record) + "(" + getString(R.string.tourguide) + ")";
                ((BaseActivity) getActivity()).getActionBar().setTitle(title);
                if (mRecordAdapter == null || !(mRecordAdapter instanceof GuideRecordAdapter)) {
                    mRecordAdapter = new GuideRecordAdapter(getActivity(), mGuideEvents);
                    mGuideEvents.clear();
                    mInviteEvents.clear();
                    mListView.setAdapter(mRecordAdapter);

                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mListView.setRefreshing(true);
                        }
                    }, 100);
                }
            } else {
                String title = getString(R.string.profile_record) + "(" + getString(R.string.tourist) + ")";
                ((BaseActivity) getActivity()).getActionBar().setTitle(title);
                if (mRecordAdapter == null || !(mRecordAdapter instanceof InviteRecordAdapter)) {
                    mRecordAdapter = new InviteRecordAdapter(getActivity(), mInviteEvents);
                    mGuideEvents.clear();
                    mInviteEvents.clear();
                    mListView.setAdapter(mRecordAdapter);

                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mListView.setRefreshing(true);
                        }
                    }, 100);
                }
            }
        }
    }

    private OnGetHistoricalInviteEventsListener mGetHistoricalInviteEventsListener = new OnGetHistoricalInviteEventsListener() {
        @Override
        public void onGetHistoricalInviteEvents(final int result, final List<InviteEvent> inviteEvents) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (result == InviteHelper.SUCCESS) {
                        mInviteEvents.clear();
                        if (inviteEvents != null) {
                            mInviteEvents.addAll(inviteEvents);
                        }
                        mRecordAdapter.notifyDataSetChanged();
                    }
                    mListView.onRefreshComplete();
                }
            });
        }
    };

    private OnGetHistoricalGuideEventsListener mGetHistoricalGuideEventsListener = new OnGetHistoricalGuideEventsListener() {
        @Override
        public void onGetHistoricalGuideEvents(final int result, final List<GuideEvent> guideEvents) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (result == GuideHelper.SUCCESS) {
                        mGuideEvents.clear();
                        if (guideEvents != null) {
                            mGuideEvents.addAll(guideEvents);
                        }
                        mRecordAdapter.notifyDataSetChanged();
                    }
                    mListView.onRefreshComplete();
                }
            });
        }
    };

    private void refresh() {
        if (mSettingManager.getUserId() > 0) {
            if (mSettingManager.getUserType() == Tourist.USER_TYPE_TOURGUIDE) {
                mGuideHelper.getHistoricalGuideEvents(0, 100, mGetHistoricalGuideEventsListener);
            } else {
                mInviteHelper.getHistoricalInviteEvents(0, 100, mGetHistoricalInviteEventsListener);
            }
        }
    }

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
