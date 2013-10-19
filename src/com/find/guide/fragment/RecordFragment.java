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

public class RecordFragment extends Fragment {

    private PullToRefreshListView mListView;

    private InviteHelper mInviteHelper = null;
    private GuideHelper mGuideHelper = null;

    private SettingManager mSettingManager;

    private BaseAdapter mRecordAdapter;
    private List<InviteEvent> mInviteEvents = new ArrayList<InviteEvent>();
    private List<GuideEvent> mGuideEvents = new ArrayList<GuideEvent>();

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private boolean mIsRefreshing = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mInviteHelper = new InviteHelper(TourGuideApplication.getInstance());
        mGuideHelper = new GuideHelper(TourGuideApplication.getInstance());

        mSettingManager = SettingManager.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record, null);

        mListView = (PullToRefreshListView) view.findViewById(R.id.listview);
        mListView.setShowIndicator(false);
        mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                refresh();
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        refresh();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            String title = "";
            if (mSettingManager.getUserId() <= 0) {
                title = getString(R.string.profile_record) + "(" + getString(R.string.unlogin) + ")";
            } else if (mSettingManager.getUserType() == Tourist.USER_TYPE_TOURGUIDE
                    && mSettingManager.getGuideMode() == 0) {
                title = getString(R.string.profile_record) + "(" + getString(R.string.tourguide) + ")";
            } else {
                title = getString(R.string.profile_record) + "(" + getString(R.string.tourist) + ")";
            }
            ((BaseActivity) getActivity()).getActionBar().setTitle(title);

            refresh();
        }
    }

    private void refresh() {
        if (mSettingManager.getUserId() <= 0) {
            if (mRecordAdapter != null) {
                mInviteEvents.clear();
                mGuideEvents.clear();
                mRecordAdapter.notifyDataSetChanged();
            }
        } else if (mSettingManager.getUserType() == Tourist.USER_TYPE_TOURGUIDE && mSettingManager.getGuideMode() == 0) {
            if (mRecordAdapter == null || !(mRecordAdapter instanceof GuideRecordAdapter)) {
                mRecordAdapter = new GuideRecordAdapter(getActivity(), mGuideEvents);
                mGuideEvents.clear();
                mInviteEvents.clear();
                mListView.setAdapter(mRecordAdapter);
            }
        } else {
            if (mRecordAdapter == null || !(mRecordAdapter instanceof InviteRecordAdapter)) {
                mRecordAdapter = new InviteRecordAdapter(getActivity(), mInviteEvents);
                mGuideEvents.clear();
                mInviteEvents.clear();
                mListView.setAdapter(mRecordAdapter);
            }
        }

        if (mSettingManager.getUserId() > 0 && !mIsRefreshing) {
            mIsRefreshing = true;
            if (mSettingManager.getUserType() == Tourist.USER_TYPE_TOURGUIDE) {
                mGuideHelper.getHistoricalGuideEvents(0, 100, mGetHistoricalGuideEventsListener);
            } else {
                mInviteHelper.getHistoricalInviteEvents(0, 100, mGetHistoricalInviteEventsListener);
            }
        } else {
            mIsRefreshing = false;
            mListView.onRefreshComplete();
        }
    }

    private OnGetHistoricalInviteEventsListener mGetHistoricalInviteEventsListener = new OnGetHistoricalInviteEventsListener() {
        @Override
        public void onGetHistoricalInviteEvents(final int result, final List<InviteEvent> inviteEvents) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mIsRefreshing = false;
                    mListView.onRefreshComplete();
                    if (result == InviteHelper.SUCCESS) {
                        mInviteEvents.clear();
                        if (inviteEvents != null) {
                            mInviteEvents.addAll(inviteEvents);
                        }
                        mRecordAdapter.notifyDataSetChanged();
                    }
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
                    mIsRefreshing = false;
                    mListView.onRefreshComplete();
                    if (result == GuideHelper.SUCCESS) {
                        mGuideEvents.clear();
                        if (guideEvents != null) {
                            mGuideEvents.addAll(guideEvents);
                        }
                        mRecordAdapter.notifyDataSetChanged();
                    }
                }
            });
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
