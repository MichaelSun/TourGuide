package com.find.guide.activity;

import java.util.Calendar;

import com.find.guide.R;
import com.find.guide.model.GuideEvent;
import com.find.guide.model.InviteEvent;
import com.find.guide.model.helper.GuideHelper;
import com.find.guide.model.helper.GuideHelper.OnRefusedListener;
import com.find.guide.model.helper.InviteHelper;
import com.find.guide.model.helper.GuideHelper.OnAcceptedListener;
import com.find.guide.model.helper.InviteHelper.OnInviteListener;
import com.find.guide.view.TipsDialog;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class OrderActivity extends BaseActivity implements OnClickListener {

    public static final String INTENT_EXTRA_USER_TYPE = "intent_extra_user_type";
    public static final int TYPE_TOURGUIDE = 1;
    public static final int TYPE_TOURIST = 2;

    public static final String INTENT_EXTRA_TOURIST_OBJ = "intent_extra_tourist_obj";

    private int mUserType = TYPE_TOURIST;
    private InviteEvent mInviteEvent = null;
    private GuideEvent mGuideEvent = null;

    private TextView mTouristLabel;
    private TextView mTouristName;
    private TextView mDestinationTv;
    private TextView mStartTimeTv;
    private TextView mEndTimeTv;
    private Button mButton1;
    private Button mButton2;

    private InviteHelper mInviteHelper = null;
    private GuideHelper mGuideHelper = null;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            if (getIntent() != null) {
                mUserType = getIntent().getIntExtra(INTENT_EXTRA_USER_TYPE, TYPE_TOURIST);
                if (mUserType == TYPE_TOURGUIDE) {
                    mGuideEvent = (GuideEvent) getIntent().getSerializableExtra(INTENT_EXTRA_TOURIST_OBJ);
                } else {
                    mInviteEvent = (InviteEvent) getIntent().getSerializableExtra(INTENT_EXTRA_TOURIST_OBJ);
                }
            }
        } else {
            mUserType = savedInstanceState.getInt(INTENT_EXTRA_USER_TYPE);
            if (mUserType == TYPE_TOURGUIDE) {
                mGuideEvent = (GuideEvent) savedInstanceState.getSerializable(INTENT_EXTRA_TOURIST_OBJ);
            } else {
                mInviteEvent = (InviteEvent) savedInstanceState.getSerializable(INTENT_EXTRA_TOURIST_OBJ);
            }
        }
        
        setContentView(R.layout.activity_order);
        initUI();

        mInviteHelper = new InviteHelper(getApplicationContext());
        mGuideHelper = new GuideHelper(getBaseContext());
        mInviteHelper.setOnInviteListener(mOnInviteListener);
    }

    private void initUI() {
        mTouristLabel = (TextView) findViewById(R.id.tourist_label);
        mTouristName = (TextView) findViewById(R.id.tourist_name);
        mDestinationTv = (TextView) findViewById(R.id.destination_tv);
        mStartTimeTv = (TextView) findViewById(R.id.start_time_tv);
        mEndTimeTv = (TextView) findViewById(R.id.end_time_tv);
        mButton1 = (Button) findViewById(R.id.order_btn1);
        mButton2 = (Button) findViewById(R.id.order_btn2);

        mButton1.setOnClickListener(this);
        mButton2.setOnClickListener(this);

        if (mUserType == TYPE_TOURGUIDE) {
            mTouristLabel.setText(R.string.tourist);
            mTouristName.setText(mGuideEvent.getUserName());
            mDestinationTv.setText(mGuideEvent.getScenic());
            mStartTimeTv.setText(getTimeStr(mGuideEvent.getStartTime()));
            mEndTimeTv.setText(getTimeStr(mGuideEvent.getEndTime()));
            mButton1.setText(R.string.accept);
            mButton2.setText(R.string.refuse);
            mButton2.setVisibility(View.VISIBLE);
        } else {
            mTouristLabel.setText(R.string.tourguide);
            mTouristName.setText(mInviteEvent.getGuideName());
            mDestinationTv.setText(mInviteEvent.getScenic());
            mStartTimeTv.setText(getTimeStr(mInviteEvent.getStartTime()));
            mEndTimeTv.setText(getTimeStr(mInviteEvent.getEndTime()));
            mButton1.setText(R.string.cancel);
            mButton2.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(INTENT_EXTRA_USER_TYPE, mUserType);
        if (mUserType == TYPE_TOURGUIDE) {
            outState.putSerializable(INTENT_EXTRA_TOURIST_OBJ, mGuideEvent);
        } else {
            outState.putSerializable(INTENT_EXTRA_TOURIST_OBJ, mInviteEvent);
        }
    }

    @Override
    protected void onDestroy() {
        if (mInviteHelper != null) {
            mInviteHelper.destroy();
            mInviteHelper = null;
        }
        if (mGuideHelper != null) {
            mGuideHelper.destroy();
            mGuideHelper = null;
        }
        TipsDialog.getInstance().dismiss();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.order_btn1:
            if (mUserType == TYPE_TOURGUIDE) {
                accept();
            } else {
                cancel();
            }
            break;
        case R.id.order_btn2:
            refuse();
            break;
        }
    }

    private String getTimeStr(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return year + "/" + (month + 1 < 10 ? "0" + (month + 1) : month + 1) + "/" + (day < 10 ? "0" + (day) : day);
    }

    private void cancel() {
        TipsDialog.getInstance().show(this, R.drawable.tips_loading, "取消中...", true, false);
        mInviteHelper.cancelInvite(mInviteEvent.getEventId(), mInviteEvent.getGuideId());
    }

    private void accept() {
        TipsDialog.getInstance().show(this, R.drawable.tips_loading, "接受中...", true, false);
        mGuideHelper.accept(mGuideEvent.getEventId(), mGuideEvent.getUserId(), mOnAcceptedListener);
    }

    private void refuse() {
        TipsDialog.getInstance().show(this, R.drawable.tips_loading, "忽略中...", true, false);
        mGuideHelper.refuse(mGuideEvent.getEventId(), mGuideEvent.getUserId(), mOnRefusedListener);
    }

    OnInviteListener mOnInviteListener = new OnInviteListener() {

        @Override
        public void onInviteAll(int result) {
        }

        @Override
        public void onInvite(int result) {
        }

        @Override
        public void onCancelInvite(final int result) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    TipsDialog.getInstance().dismiss();
                    if (result == InviteHelper.SUCCESS) {
                        finish();
                    } else {
                        // TODO
                    }
                }
            });
        }
    };

    OnAcceptedListener mOnAcceptedListener = new OnAcceptedListener() {

        @Override
        public void onAccepted(final int result) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    TipsDialog.getInstance().dismiss();
                    if (result == GuideHelper.SUCCESS) {
                        finish();
                    } else {

                    }
                }
            });
        }
    };

    OnRefusedListener mOnRefusedListener = new OnRefusedListener() {

        @Override
        public void onRefused(final int result) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    TipsDialog.getInstance().dismiss();
                    if (result == GuideHelper.SUCCESS) {
                        finish();
                    } else {

                    }
                }
            });
        }
    };

}
