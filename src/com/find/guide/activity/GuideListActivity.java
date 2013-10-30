package com.find.guide.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.find.guide.R;
import com.find.guide.adapter.GuideAdapter;
import com.find.guide.user.TourGuide;
import com.find.guide.user.UserHelper;
import com.find.guide.user.UserHelper.OnSearchGuideListener;
import com.find.guide.view.TipsDialog;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

@SuppressLint("HandlerLeak")
public class GuideListActivity extends BaseActivity {

    public static final String INTENT_EXTRA_CITY_INT = "intent_extra_city_int";
    public static final String INTENT_EXTRA_GENDER_INT = "intent_extra_gender_int";
    public static final String INTENT_EXTRA_SCENIC_STRING = "intent_extra_scenic_string";

    private int mCityNo;
    private int mGender;
    private String mScenic;

    private PullToRefreshListView mListView;

    private GuideAdapter mGuideAdapter;

    private List<TourGuide> mTourGuides = new ArrayList<TourGuide>();

    private UserHelper mUserHelper = null;

    private static final int ROWS = 21;

    private static final int WHAT_SEARCH_GUIDE_SUCCESS = 1;
    private static final int WHAT_SEARCH_MORE_GUIDE_SUCCESS = 2;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == WHAT_SEARCH_GUIDE_SUCCESS) {
                TipsDialog.getInstance().dismiss();
                int result = msg.arg1;
                if (result == UserHelper.SUCCESS) {
                    @SuppressWarnings("unchecked")
                    List<TourGuide> guides = (List<TourGuide>) msg.obj;

                    if (guides == null || guides.size() == 0) {
                        TipsDialog.getInstance().show(GuideListActivity.this, R.drawable.tips_fail,
                                "找不到您需要的导游，请放宽点条件试试看？", true);
                        mListView.setMode(Mode.DISABLED);
                    } else {
                        if (guides.size() >= ROWS) {
                            mListView.setMode(Mode.PULL_FROM_END);
                            guides.remove(guides.size() - 1);
                        } else {
                            mListView.setMode(Mode.DISABLED);
                        }
                        mTourGuides.clear();
                        mTourGuides.addAll(guides);
                        mGuideAdapter.notifyDataSetChanged();
                    }
                }
            } else if (msg.what == WHAT_SEARCH_MORE_GUIDE_SUCCESS) {
                mListView.onRefreshComplete();
                int result = msg.arg1;
                if (result == UserHelper.SUCCESS) {
                    @SuppressWarnings("unchecked")
                    List<TourGuide> guides = (List<TourGuide>) msg.obj;
                    if (guides == null || guides.size() == 0) {
                        mListView.setMode(Mode.DISABLED);
                    } else {
                        if (guides.size() >= ROWS) {
                            mListView.setMode(Mode.PULL_FROM_END);
                            guides.remove(guides.size() - 1);
                        } else {
                            mListView.setMode(Mode.DISABLED);
                        }
                        mTourGuides.addAll(guides);
                        mGuideAdapter.notifyDataSetChanged();
                    }
                }
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_guide_list);
        initUI();

        if (savedInstanceState != null) {
            mCityNo = savedInstanceState.getInt(INTENT_EXTRA_CITY_INT);
            mCityNo = savedInstanceState.getInt(INTENT_EXTRA_GENDER_INT);
            mScenic = savedInstanceState.getString(INTENT_EXTRA_SCENIC_STRING);
        } else {
            if (getIntent() != null) {
                mCityNo = getIntent().getIntExtra(INTENT_EXTRA_CITY_INT, 0);
                mGender = getIntent().getIntExtra(INTENT_EXTRA_GENDER_INT, 0);
                mScenic = getIntent().getStringExtra(INTENT_EXTRA_SCENIC_STRING);
            }
        }

        mUserHelper = new UserHelper(getApplicationContext());

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                TipsDialog.getInstance().show(GuideListActivity.this, R.drawable.tips_loading, R.string.searching,
                        true, false);
                mUserHelper.searchGuide(mCityNo, mGender, mScenic, 0, ROWS, mOnSearchGuideListener);
            }
        }, 300);
    }

    private OnSearchGuideListener mOnSearchGuideListener = new OnSearchGuideListener() {

        @Override
        public void onSearchGuide(int result, List<TourGuide> guides) {
            Message msg = Message.obtain();
            msg.what = WHAT_SEARCH_GUIDE_SUCCESS;
            msg.arg1 = result;
            msg.obj = guides;
            mHandler.sendMessage(msg);
        }

        @Override
        public void onSearchMoreGuide(int result, List<TourGuide> guides) {
            Message msg = Message.obtain();
            msg.what = WHAT_SEARCH_MORE_GUIDE_SUCCESS;
            msg.arg1 = result;
            msg.obj = guides;
            mHandler.sendMessage(msg);
        }
    };

    private void initUI() {
        mListView = (PullToRefreshListView) findViewById(R.id.listview);
        mGuideAdapter = new GuideAdapter(this, mTourGuides);
        mListView.setAdapter(mGuideAdapter);

        mListView.setShowIndicator(false);
        mListView.setMode(Mode.DISABLED);
        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                loadMore();
            }
        });

        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int pos = position - mListView.getRefreshableView().getHeaderViewsCount();
                if (pos >= 0 && pos < mTourGuides.size()) {
                    TourGuide guide = mTourGuides.get(pos);
                    Intent intent = new Intent(GuideListActivity.this, BookingActivity.class);
                    intent.putExtra(BookingActivity.INTENT_EXTRA_GUIDE, guide);
                    startActivity(intent);
                }
            }
        });

    }

    private void loadMore() {
        int start = 0;
        if (mTourGuides != null) {
            start = mTourGuides.size();
        }
        mUserHelper.searchGuide(mCityNo, mGender, mScenic, start, ROWS, mOnSearchGuideListener);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(INTENT_EXTRA_CITY_INT, mCityNo);
        outState.putInt(INTENT_EXTRA_GENDER_INT, mGender);
        outState.putString(INTENT_EXTRA_SCENIC_STRING, mScenic);
    }

    @Override
    protected void onDestroy() {
        if (mUserHelper != null) {
            mUserHelper.destroy();
            mUserHelper = null;
        }
        super.onDestroy();
    }

}
