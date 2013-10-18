package com.find.guide.activity;

import java.util.ArrayList;
import java.util.List;

import com.find.guide.R;
import com.find.guide.adapter.GuideAdapter;
import com.find.guide.model.TourGuide;
import com.find.guide.model.helper.UserHelper;
import com.find.guide.model.helper.UserHelper.OnSearchGuideListener;
import com.find.guide.view.TipsDialog;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.ListView;

@SuppressLint("HandlerLeak")
public class GuideListActivity extends BaseActivity {

    public static final String INTENT_EXTRA_CITY_INT = "intent_extra_city_int";
    public static final String INTENT_EXTRA_GENDER_INT = "intent_extra_gender_int";
    public static final String INTENT_EXTRA_SCENIC_STRING = "intent_extra_scenic_string";

    private int mCityNo;
    private int mGender;
    private String mScenic;

    private ListView mListView;

    private GuideAdapter mGuideAdapter;

    private List<TourGuide> mTourGuides = new ArrayList<TourGuide>();

    private UserHelper mUserHelper = null;

    private static final int WHAT_SEARCH_GUIDE_SUCCESS = 1;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == WHAT_SEARCH_GUIDE_SUCCESS) {
                TipsDialog.getInstance().dismiss();
                int result = msg.arg1;
                if (result == UserHelper.SEARCH_GUIDE_SUCCESS) {
                    @SuppressWarnings("unchecked")
                    List<TourGuide> guides = (List<TourGuide>) msg.obj;
                    mTourGuides.clear();
                    if (guides != null)
                        mTourGuides.addAll(guides);
                    mGuideAdapter.notifyDataSetChanged();
                } else {

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
                mUserHelper.searchGuide(mCityNo, mGender, mScenic, 0, 100, mOnSearchGuideListener);
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
    };

    private void initUI() {
        mListView = (ListView) findViewById(R.id.listview);
        mGuideAdapter = new GuideAdapter(this, mTourGuides);
        mListView.setAdapter(mGuideAdapter);
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
