package com.find.guide.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ListView;

import com.find.guide.R;
import com.find.guide.adapter.GuideRecordAdapter;
import com.find.guide.adapter.GuideRecordAdapter.VisitMode;
import com.find.guide.model.GuideEvent;
import com.find.guide.model.helper.GuideHelper;
import com.find.guide.model.helper.GuideHelper.OnGetHistoricalGuideEventsListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class GuideRecordActivity extends BaseActivity {

    public static final String EXTRA_INTENT_GUIDE_ID = "intent_extra_guide_id";

    private int mUserId = 0;

    private PullToRefreshListView mListView;

    private GuideRecordAdapter mGuideRecordAdapter;

    private List<GuideEvent> mGuideEvents = new ArrayList<GuideEvent>();

    private GuideHelper mGuideHelper = null;

    private boolean mIsRefreshing = false;

    private static final int ROWS = 21;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_record);

        if (getIntent() != null) {
            mUserId = getIntent().getIntExtra(EXTRA_INTENT_GUIDE_ID, 0);
        }
        if (mUserId <= 0) {
            finish();
            return;
        }

        initUI();

        mGuideHelper = new GuideHelper(getApplicationContext());

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mListView.setRefreshing(true);
            }
        }, 300);
    }

    private void initUI() {
        mListView = (PullToRefreshListView) findViewById(R.id.listview);
        mListView.setShowIndicator(false);
        mListView.setMode(Mode.PULL_FROM_START);
        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                if (!mIsRefreshing) {
                    mIsRefreshing = true;
                    mGuideHelper.getHistoricalGuideEvents(mUserId, 0, ROWS, mOnGetHistoricalGuideEventsListener);
                }
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                if (!mIsRefreshing) {
                    mIsRefreshing = true;
                    int start = 0;
                    if (mGuideEvents != null) {
                        start = mGuideEvents.size();
                    }
                    mGuideHelper.getHistoricalGuideEvents(mUserId, start, ROWS, mOnGetHistoricalGuideEventsListener);
                }
            }
        });

        mGuideRecordAdapter = new GuideRecordAdapter(this, VisitMode.OTHERS, mGuideEvents);
        mListView.setAdapter(mGuideRecordAdapter);
    }

    OnGetHistoricalGuideEventsListener mOnGetHistoricalGuideEventsListener = new OnGetHistoricalGuideEventsListener() {

        @Override
        public void onGetHistoricalGuideEvents(final int result, final List<GuideEvent> guideEvents) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mListView.onRefreshComplete();
                    mIsRefreshing = false;
                    if (result == GuideHelper.SUCCESS) {
                        if (guideEvents != null && guideEvents.size() > 0) {
                            mGuideEvents.clear();
                            if (guideEvents.size() >= ROWS) {
                                mListView.setMode(Mode.BOTH);
                                guideEvents.remove(guideEvents.size() - 1);
                            } else {
                                mListView.setMode(Mode.PULL_FROM_START);
                            }
                            mGuideEvents.addAll(guideEvents);
                            mGuideRecordAdapter.notifyDataSetChanged();
                        } else {
                            mListView.setMode(Mode.PULL_FROM_START);
                        }

                    }
                }
            });
        }

        @Override
        public void onGetMoreHistoricalGuideEvents(final int result, final List<GuideEvent> guideEvents) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mListView.onRefreshComplete();
                    mIsRefreshing = false;
                    if (result == GuideHelper.SUCCESS) {
                        if (guideEvents != null && guideEvents.size() > 0) {
                            if (guideEvents.size() >= ROWS) {
                                mListView.setMode(Mode.BOTH);
                                guideEvents.remove(guideEvents.size() - 1);
                            } else {
                                mListView.setMode(Mode.PULL_FROM_START);
                            }
                            mGuideEvents.addAll(guideEvents);
                            mGuideRecordAdapter.notifyDataSetChanged();
                        } else {
                            mListView.setMode(Mode.PULL_FROM_START);
                        }
                    }
                }
            });
        }
    };

    @Override
    protected void onDestroy() {
        if (mGuideHelper != null) {
            mGuideHelper.destroy();
            mGuideHelper = null;
        }
        super.onDestroy();
    }

}
